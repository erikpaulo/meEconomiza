define(['./module'], 
function (module) {
	
	module.factory('AccountEntryResource', function($resource) {
	    return $resource(
	        'api/account/:type/:accountId/entry/:id/:action',
	        {id: '@id', accountId: '@accountId', type: '@type'},
	        {

	        }
	    );
	});
});