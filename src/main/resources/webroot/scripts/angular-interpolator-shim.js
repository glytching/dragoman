window.dragoman = angular.module('dragoman', ['ngMaterial', 'ngMessages', 'jsonFormatter']);

dragoman.config(function($interpolateProvider, $mdThemingProvider){
    $mdThemingProvider.theme('docs-dark', 'default')
        .primaryPalette('yellow')
        .dark();
    $interpolateProvider.startSymbol('{[{').endSymbol('}]}');
});

dragoman.config(function (JSONFormatterConfigProvider) {
    // https://github.com/mohsen1/json-formatter/
    JSONFormatterConfigProvider.hoverPreviewEnabled = true;
    JSONFormatterConfigProvider.hoverPreviewFieldCount = 5;
});