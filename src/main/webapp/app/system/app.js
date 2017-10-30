define(['angular',
        'layout-core',
        'system/routes',
        'system/components/dependency-resolver',
        'system/components/error-handler',
        'system/components/auth-handler',
        'system/components/index-handler',
        'system/components/interceptors',
        'system/directives/index',
        'system/filters/index'],
function(angular, layout, config, dependencyResolverFor, errorHandler, authHandler, indexHandler, InterceptorModule)
{
    
	// TODO [marcus] app.directives foi iniciada no arquivo 'system/directives/index'. Alterar esse index para retornar o módulo e pegar o nome aqui para evitar hardcoded
	
    var app = angular.module('app', [
        'ngRoute',
        'ngAnimate',
        'ngResource',
        'ngCookies',
        'ngLocale',

        'ngMessages',

        'ngMaterial', 'ngAria', 'ngMdIcons',

        'highcharts-ng',

        'ngFileUpload',

        errorHandler.name,
        authHandler.name,
        indexHandler.name,
        InterceptorModule.name,
        'app.directives',
        'app.filters'
    ]);

    app.config(
    [
        '$routeProvider',
        '$locationProvider',
        '$controllerProvider',
        '$compileProvider',
        '$filterProvider',
        '$provide',
        '$mdThemingProvider',
        '$mdDateLocaleProvider',

        function($routeProvider, $locationProvider, $controllerProvider, $compileProvider, $filterProvider, $provide, $mdThemingProvider, $mdDateLocaleProvider)
        {
            app.controller = $controllerProvider.register;
            app.directive  = $compileProvider.directive;
            app.filter     = $filterProvider.register;
            app.factory    = $provide.factory;
            app.service    = $provide.service;
            app.routeProvider = $routeProvider;
            app.modules    = [];

            $routeProvider.when('/login', {templateUrl : 'system/views/login.html'});

            $routeProvider.when('/404', {templateUrl : 'system/views/error/404.html'});
            $routeProvider.when('/403', {templateUrl : 'system/views/error/403.html'});
            $routeProvider.when('/500', {templateUrl : 'system/views/error/500.html'});

            // EXTENSION-POINT: DEMAIS ROTAS PODEM SER ADICIONAS A PARTIS DO $scope.modules

            // Redireciona para página default.
            $routeProvider.otherwise({redirectTo: '/'});

            // Configuring default Material Design Theme
            $mdThemingProvider.theme('default').accentPalette('blue-grey');

            $compileProvider.debugInfoEnabled(true);
        }
    ]);
    
    app.run(function ($rootScope, $http) {
    	
    	app.modules = $rootScope.modules;
    	app.context = $rootScope.appContext;
        
        $rootScope.loadRoutes = function(modules) {
        	// rotas padrão da system
    		if(config.routes !== undefined) {
  			  angular.forEach(config.routes, function(route, path){
  				  app.routeProvider.when(path, {templateUrl:route.templateUrl, resolve:dependencyResolverFor(route.dependencies)});
  			  });
    		}	        
        	
        	// rotas de cada módulo
        	angular.forEach(modules, function(module){
        		  if (module.originalPath != undefined) {
        			  app.routeProvider.when(module.originalPath, {templateUrl:module.templateUrl, resolve:dependencyResolverFor(module.dependencies)});
        		  }
	              angular.forEach(module.items, function(item){
	            	app.routeProvider.when(item.originalPath, {templateUrl:item.templateUrl, resolve:dependencyResolverFor(item.dependencies)});
	              });
        	});
        }
        
        // carrega as rotas
        $rootScope.loadRoutes($rootScope.modules);
        
    });

    app.service('Constants', function() {
		this.ACCOUNT = {
		    TYPE: {
                CKA: {id: 'CKA',  name: 'Conta Corrente'}, // checking account
                SVA: {id: 'SVA',  name: 'Conta Poupança'}, // saving account
                INV: {id: 'INV',  name: 'Conta Investimento',
                    type:{
                        ST:  {id: 'ST',  name: 'Ações'}, // stocks
                        DB:  {id: 'DB',  name: 'Debêntures'}, // debentures
                        FCP: {id: 'FCP', name: 'Fundo Curto Prazo'}, // fundo short-term
                        FR:  {id: 'FR',  name: "Fundo Referenciado"},
                        FRF: {id: 'FRF', name: 'Fundo de Renda Fixa'},
                        FA:  {id: 'FA',  name: 'Fundo de Ações'},
                        FC:  {id: 'FC',  name: 'Fundo Cambial'},
                        FDE: {id: 'FDE', name: 'Fundo de Dívida Externa'},
                        FM:  {id: 'FM',  name: 'Fundo Multimercado'}
                    }
                }, // investment account
                CCA: {id: 'CCA', name: 'Cartão de Crédito'} // credit card account
            }
		}
	});


   return app;
});