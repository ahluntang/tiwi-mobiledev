module.exports = function(grunt) {
  require('load-grunt-tasks')(grunt);

  var path = require('path');
  var src = path.resolve(__dirname, 'src');
  var dest = path.resolve(__dirname, 'public');
  var cordova = path.resolve(__dirname, 'native/www');

  grunt.initConfig({
    copy: {
      root: {
        cwd: src,
        src: '*',
        dest: dest,
        expand: true,
        filter: 'isFile'
      },
      assets: {
        cwd: src + '/assets',
        src: '**',
        dest: dest + '/assets',
        expand: true
      },
      testdata: {
        cwd: src + '/test',
        src: '**',
        dest: dest + '/test',
        expand: true,
        filter: 'isFile'
      },
      cordova: {
        cwd: dest,
        src: '**',
        dest: cordova,
        expand: true
      }
    },
    htmlmin: {
      options: {
        removeComments: true,
        collapseWhitespace: true
      },
      dist: {
        src: src + '/index.html',
        dest: dest + '/index.html'
      }
    },
    sass: {
      dist: {
        src: src + '/css/app.scss',
        dest: dest + '/css/app.css'
      }
    },
    csso: {
      dist: {
        src: dest + '/css/app.css',
        dest: dest + '/css/app.css'
      }
    },
    autoprefixer: {
      dist: {
        src: dest + '/css/app.css'
      }
    },
    jshint: {
      src: src + '/js/99-app.js',
      options: {
        browser: true,
        curly: true,
        devel: true,
        eqeqeq: true,
        forin: true,
        indent: 2,
        latedef: true,
        newcap: true,
        noarg: true,
        strict: true,
        trailing: true,
        undef: true,
        unused: "vars",
        globals: {
          jQuery: true,
          Mustache: true
        }
      }
    },
    concat: {
      dist: {
        src: src + '/js/**/*.js',
        dest: dest + '/js/app.js'
      }
    },
    removelogging: {
      dist: {
        src: dest + '/js/app.js'
      }
    },
    uglify: {
      dist: {
        src: dest + '/js/app.js',
        dest: dest + '/js/app.js'
      }
    },
    appcache: {
      options: {
        basePath: dest
      },
      dist: {
        dest: dest + '/manifest.appcache',
        cache: [ dest + '/**/*', '!' + dest + '/manifest.appcache' ],
        network: [ '*', 'http://*', 'https://*' ],
        fallback: '/ /index.html'
      }
    },
    'string-replace': {
      cordova: {
        src: dest + '/index.html',
        dest: cordova + '/index.html',
        options: {
          replacements: [{
            pattern: /(<script\b)/,
            replacement: '<script src="cordova.js"></script>$1'
          }, {
            pattern: /(<html\b[^>]*)\smanifest=.*?/,
            replacement: '$1'
          }]
        }
      }
    },
    shell: {
      debug: {
        command: 'cordova build android --debug',
        options: {
          stdout: true,
          stderr: true,
          execOptions: {
            cwd: cordova
          }
        }
      },
      dist: {
        command: 'cordova build android --release',
        options: {
          stdout: true,
          stderr: true,
          execOptions: {
            cwd: cordova
          }
        }
      }
    },
    clean: {
      dist: dest,
      cordova_appcache: cordova + '/manifest.appcache'
    },
    watch: {
      root: {
        files: src + '/*',
        tasks: 'copy:root',
        options: {
          livereload: true
        }
      },
      assets: {
        files: src + '/assets/**/*',
        tasks: 'copy:assets',
        options: {
          livereload: true
        }
      },
      testdata: {
        files: src + '/test/**/*',
        tasks: 'copy:testdata',
        options: {
          livereload: true
        }
      },
      html: {
        files: src + '/index.html',
        tasks: 'debug:html',
        options: {
          livereload: true
        }
      },
      css: {
        files: src + '/css/**/*',
        tasks: 'debug:css',
        options: {
          livereload: true
        }
      },
      js: {
        files: src + '/js/**/*.js',
        tasks: 'debug:js',
        options: {
          livereload: true
        }
      }
    },
    express: {
      dist: {
        options: {
          bases: dest,
          livereload: true
        }
      }
    }
  });

  grunt.registerTask('preflight',   [ 'copy:root', 'copy:assets' ]);

  grunt.registerTask('debug:html',  [ 'htmlmin' ]);
  grunt.registerTask('debug:css',   [ 'sass', 'autoprefixer' ]);
  grunt.registerTask('debug:js',    [ 'concat' ]);

  grunt.registerTask('dist:html',   [ 'htmlmin' ]);
  grunt.registerTask('dist:css',    [ 'sass', 'csso', 'autoprefixer' ]);
  grunt.registerTask('dist:js',     [ 'concat', 'removelogging', 'uglify' ]);

  grunt.registerTask('build:debug', [
                                      'clean',
                                      'preflight', 'copy:testdata',
                                      'debug:html', 'debug:css', 'debug:js'
                                    ]);
  grunt.registerTask('build:dist',  [
                                      'clean',
                                      'preflight',
                                      'dist:html', 'dist:css', 'dist:js'
                                    ]);

  grunt.registerTask('debug',       [ 'build:debug', 'express', 'watch' ]);
  grunt.registerTask('dist',        [ 'build:dist' ]);

  grunt.registerTask('cordova:setup', [
                                        'copy:cordova',
                                        'clean:cordova_appcache',
                                        'string-replace:cordova'
                                      ]);
  grunt.registerTask('cordova:debug', [
                                        'build:debug',
                                        'cordova:setup',
                                        'shell:debug'
                                      ]);
  grunt.registerTask('cordova:dist',  [
                                        'build:dist',
                                        'cordova:setup',
                                        'shell:dist'
                                      ]);

  grunt.registerTask('heroku:production', 'dist');

  grunt.registerTask('default', 'debug');
};
