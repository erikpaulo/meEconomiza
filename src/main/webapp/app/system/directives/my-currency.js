define(['./module'], function(directives) {
    directives.directive('myCurrency', function() {
        return {
            restrict: 'AE',
            compile: function(tElem,attrs) {
                angular.element(tElem).append('<md-icon ng-if="!appContext.currencyVisible" class="material-icons step visibility-icon">visibility_off</md-icon>')
                angular.element(tElem).append('<span ng-if="appContext.currencyVisible">{{'+ attrs.myCurrency +' | currency}}</span>')

            }
        };
    });
});
