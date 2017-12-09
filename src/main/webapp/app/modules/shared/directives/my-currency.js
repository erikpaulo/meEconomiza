define(['./module'], function(directives) {
    directives.directive('myCurrency', function() {
        return {
            restrict: 'AE',
            replace: true,
            template: '<md-icon class="material-icons step md-18">visibility_off</md-icon>',
            link: function(scope, elem, attrs) {
                console.log('Link')
                //      elem.bind('click', function() {
                //        elem.css('background-color', 'white');
                //        scope.$apply(function() {
                //          scope.color = "white";
                //        });
                //      });
                //      elem.bind('mouseover', function() {
                //        elem.css('cursor', 'pointer');
                //      });
            },
            compile: function(tElem,attrs) {
                console.log('compile 1')
                //do optional DOM transformation here
                return function(scope,elem,attrs) {
                //linking function here
                    console.log('compile 2')
                };
            }
        };
    });
}
