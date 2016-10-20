requirejs.config({
    shim: {
        'jquery': {
            exports: '$'
        },
        'jquery-ui': {
            deps: ['jquery']
        },
        'gapi': {
            exports: 'gapi'
        }
    },
    paths: {
        'jquery': 'http://code.jquery.com/jquery-3.1.1.min',
        'jquery-ui': 'http://code.jquery.com/ui/1.12.1/jquery-ui.min',
        'gapi': 'https://apis.google.com/js/platform'
    }
});

define(['gapi'], function (gapi) {
    console.log('Gapi');
    gapi.load('auth2', function () {
        console.log('Gapi2');
        gapi.auth2.init({
            client_id: '728919834589-6e41p6kek58pe4honddltevel30cusuo.apps.googleusercontent.com'
        });
    });
});