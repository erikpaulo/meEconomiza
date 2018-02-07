define(['./module'], 
function (module) {
	
	module.factory('CashFlowResource', function($resource) {
	    return $resource(
	        'api/cashFlow/:action',
	        {},
	        {
	            consolidate: { method :'GET',  params: {action: 'consolidate'}, isArray : false }
	        }
	    );
	});
});