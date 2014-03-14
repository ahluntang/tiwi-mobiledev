/**
 * ParkingBuddy app
 * @author  Ah-Lun Tang
 */
(function() {

    var template;
    var parkings;
    var refresh; // refresh time 
    var interval;

    var success = function(response) {
        parkings = response["Parkings11"]['parkings'];
        parkings.sort(function(a, b){
            var nameA=a.name.toLowerCase(),
                nameB=b.name.toLowerCase();

            if (nameA < nameB) //sort string ascending
                return -1 
            if (nameA > nameB)
                return 1
            return 0 //default return value (no sorting)
        })
        $("#parkingresults ul").empty();
        $.each(parkings, function(index, garage) {
            garage = parse_garage(garage);
            var rendered_garage = Mustache.render(template, garage);
            $("#parkingresults ul").append(rendered_garage);
        });
        filter_parkings();
    }

    var parse_garage = function(garage){
        if(garage.availableCapacity === "VOL"){
            garage.remainingCapacity = 0;
            garage.status = "full";
        } else {
            garage.remainingCapacity = garage.totalCapacity - garage.availableCapacity;
            garage.status = "available";
        }
        garage.percentage = 100 * garage.remainingCapacity / garage.totalCapacity;
        //console.log(garage.remainingCapacity + ' / ' + garage.totalCapacity + ' = ' + garage.percentage);
        return garage;
    }
    var fail = function(jqXHR, status) {
        console.log('Error: loading data: ' + status);
        $("#parkingresults ul").empty();
        $("#parkingresults ul").html('<li>Error: loading data: ' + status +'</li>');
    }

    var get_parkings = function(){
        console.log("retrieving list");
        $.ajax({
            url:"test/parkings.json",
            type:"GET",
            dataType: "json",
        }).done(success).fail(fail);
    }

    var set_filter_listener = function(){
        $('#search_input').keyup(filter_parkings);
    }

    var filter_parkings = function(){
       var keyword = $('#search_input').val().toLowerCase();
       if(keyword == ""){
            $('#parkingresults > ul li').show();           
        } else {
            $('#parkingresults > ul li').each(function(){
                var text = $(this).find('h2').text().toLowerCase();
                text += $(this).find('.address').text().toLowerCase();
                (text.indexOf(keyword) >= 0) ? $(this).show() : $(this).hide();
            });
       };
    }

    var start_timer = function(){
        refresh = 10;
        interval = setInterval(timer_handler, 1000);
    }

    var timer_handler = function() {
        if (refresh == 10) {
            get_parkings();
        }
        if (refresh == 0) {
            clearInterval(interval);
            start_timer();
        } else {
            refresh--;
            console.log('decrementing: ' + refresh);
        }

        $('#interval div div').width(refresh*10 + '%' );

    }


    var application = function(){
        $.get("assets/garage.view.html",function (tmpl){
            template = tmpl;
            set_filter_listener();
            start_timer();
        })
    }
    
    application();
}());