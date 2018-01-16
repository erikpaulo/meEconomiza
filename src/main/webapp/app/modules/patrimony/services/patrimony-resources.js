define(['./module'], 
function (module) {
	
	module.factory('PatrimonyResource', function($resource) {
	    return $resource(
	        'api/patrimony/:id/:action',
	        {id: '@id'},
	        {
	            baseline: { method :'POST',  params: {action: 'baseline'}, isArray : false }
	        }
	    );
	});
});