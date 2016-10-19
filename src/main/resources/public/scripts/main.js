requirejs.config({
    paths: {
        'jquery': 'http://code.jquery.com/jquery-3.1.1.min',
        'jquery-ui': 'http://code.jquery.com/ui/1.12.1/jquery-ui.min'
    },
  shim: {
        'jquery': {
            exports: '$'
        },
        'jquery-ui': {
            deps: ['jquery']
        }
  }
});

