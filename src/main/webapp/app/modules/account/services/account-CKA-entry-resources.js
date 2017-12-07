define(['./module'], 
function (module) {
	
	module.factory('AccountEntryResource', function($resource) {
	    return $resource(
	        'api/account/CKA/:accountId/entry/:id/:action',
	        {id: '@id', accountId: '@accountId'},
	        {
	        }
	    );
	});
});