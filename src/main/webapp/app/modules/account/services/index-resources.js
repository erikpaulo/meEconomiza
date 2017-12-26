define(['./module'], 
function (module) {
	
	module.factory('IndexResource', function($resource) {
	    return $resource(
	        'api/account/INV/:accountId/index/:id/:action',
	        {accountId: '@accountId'},
	        {
                save: { method :'POST',  params: {}, isArray : false }
	        }
	    );
	});
});