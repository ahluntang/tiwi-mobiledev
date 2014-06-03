
(function($) {
	'use strict';
	//var SOURCE_URL = 'http://datatank.gent.be/Mobiliteitsbedrijf/Parkings11.json';
	var SOURCE_URL = 'test/parkings.json';
	var DATA_PATH = [ 'Parkings11', 'parkings' ];
	var garages;
    var position;
	var nodes = {};
	var garageTemplate;
	var UPDATE_INTERVAL = 50000;
    var CACHE_INTERVAL = 150 * 1000;


    var updateLocation = function (pos) {
        position = pos;
    };

	// Set the variable garages from JSON
	var setParkings = function(data) {
		DATA_PATH.forEach(function(key) {
			data = data[key];
		});
		data.forEach(function(garage) {
            if(typeof position === 'object') {
                garage.distance = haversine(position.coords.latitude, position.coords.longitude, garage.latitude, garage.longitude);
            }
			// Workaround for API bug
			if (garage.full || garage.availableCapacity === 'VOL') {
				garage.full = true;
				garage.availableCapacity = 0;
			}
		});
		data.sort(function(a, b) {
			return (a.distance < b.distance) ? -1
					: (a.distance === b.distance) ? 0 : 1;
		});
		garages = data;
	};
	
	// Initialize the DOM model from garages (needs to be set first). This needs to be called only once.
	var initializeUI = function() {
		garageTemplate = $('#garage-template').text();
		var resultsNode = $('<ul/>');
		garages.forEach(function(garage) {
			nodes[garage.name] = $('<li/>').appendTo(resultsNode);
		});
		renderParkings();

		$('#results').html(resultsNode);
		$('#status').remove();
		$('#no-results').hide();
		$('#ui').show();
		$('#search-field').on('keyup', processQuery).focus();
	};
	
	// Update dynamic properties from garages.
	var renderParkings = function() {
        var count = 0;
		garages.forEach(function(garage) {
            if(count === 0 && garage.availableCapacity > 0){
                garage.nearest = true; // since the dataset is sorted by distance
                count++;
            }
			garage.occupation = 100 - 100 * garage.availableCapacity / garage.totalCapacity;
			nodes[garage.name].html(Mustache.render(garageTemplate, garage));
			delete garage['occupation'];
		});
        count = 0;
	};
	
	// AJAX request to load new data
	// initialize parameter: true only for the first time this method is called.
	var retrieveData = function(initialize) {
        var cache_valid = false;
        var data = null;
        try {
            var cache_object = JSON.parse(localStorage.getItem("parkcache"));
            if (typeof cache_object === 'object'){
                var old = cache_object.created,
                    now = new Date().getTime(),
                    data = cache_object.value;
                var delta = now - old;
                console.log("delta:" + delta);
                if( delta < CACHE_INTERVAL){
                    cache_valid = true;
                }
            }
        } catch (error) {
            // could not retrieve cache object
        }
        if(cache_valid) {
            console.log("cache is valid, reusing data.");
            update(data,initialize);
        } else {
            console.log("cache not valid, fetching data.");
            $.getJSON(SOURCE_URL)
            // Called when json is successfully fetched.
            .done(function(data) {
                var cache = { 'created': new Date().getTime().toString() ,
                               'value': data };
                localStorage.setItem("parkcache", JSON.stringify(cache));
                update(data, initialize);
            })
            // Called when json fetch returns with error.
            .fail(function(jqXHR, textStatus) {
                $('#status').text('Initialization failed: ' + textStatus);
                $('#status').show();
            });
         }
	};

	// Callback method for search functionality.
	var processQuery = function(event) {
		var query = (event) ? event.target.value.trim().toLowerCase() : '';
		console.log('Processing query "' + query + '"');
		var matches = false;
		garages.forEach(function(garage) {
			if (query === ''
					|| garage.description.toLowerCase().indexOf(query) >= 0) {
				nodes[garage.name].show('fast');
				matches = true;
			} else {
				nodes[garage.name].hide('fast');
			}
		});
		if (matches) {
			$('#no-results').hide();
		} else {
			$('#no-results').show('fast');
		}
	};

	// Update data
	// data: returned JSON
	// initialize: true or false => true if UI needs to be initialized.
	var update = function(data, initialize) {
		setParkings(data);
		if (initialize) {
			initializeUI();
		}
		renderParkings();
	};

	var haversine =function(lat1, lon1, lat2, lon2) { 
        if(!lat1 || !lon1 || !lat1 || !lon2 ) {
            return null;
        }
	    var R = 6371; // km
		var dLat = deg2rad(lat2 - lat1);
		var dLon = deg2rad(lon2 - lon1);

		lat1 = deg2rad(lat1);
		lat2 = deg2rad(lat2);

		var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
				Math.sin(dLon / 2) * Math.sin(dLon / 2) * 
				Math.cos(lat1) * Math.cos(lat2);

		var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return R * c; 
	};

    var deg2rad = function(deg) {
		return deg * (Math.PI / 180);
	};
	
    // Run application
	var runApp = function() {
		console.log('Starting application');
        // retrieve location
        if (Modernizr.geolocation) {
            navigator.geolocation.getCurrentPosition(updateLocation);
        }
        Modernizr.load({
            test: Modernizr.localstorage,
            nope: 'polyfills/textStorage.js'
        });
		retrieveData(true);
		setInterval(retrieveData, UPDATE_INTERVAL);
	};

	$(document).ready(runApp);
   
    //document.addEventListener('deviceready', runApp, false);
})(jQuery);
