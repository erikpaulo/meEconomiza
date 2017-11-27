define(['./module'], 
function (module) {
	
	module.factory('ConciliationResource', function($resource) {
	    return $resource(
	        'api/account/:accountId/conciliation/:id/:action',
	        {accountId: '@accountId', id: '@id'},
	        {
	            syncIntoAccount: { method :'POST',  params: {action: "sync"}, isArray : false },
	            rollback: { method :'POST',  params: {action: "rollback"}, isArray : false },
	        }
	    );
	});
});