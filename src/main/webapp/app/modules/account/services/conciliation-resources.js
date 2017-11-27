define(['./module'], 
function (module) {
	
	module.factory('AccountResource', function($resource) {
	    return $resource(
	        'api/account/:id/:action',
	        {id: '@id'},
	        {
	            listAll:            { method :'GET',  params: {}, isArray : true },
	            getConciliations:   { method: 'GET',  params: {action: 'conciliation'}, isArray: true}
	        }
	    );
	});
});