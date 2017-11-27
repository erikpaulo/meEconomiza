define(['./module'], 
function (module) {
	
	module.factory('ConciliationResource', function($resource) {
	    return $resource(
	        'api/account/conciliation/:id',
	        {id: '@id'},
	        {
	            delete:     { method :'DELETE',  params: {}, isArray : true },
	            get:		{ method :'GET',  params: {}, isArray : false },
	        }
	    );

	});

});	