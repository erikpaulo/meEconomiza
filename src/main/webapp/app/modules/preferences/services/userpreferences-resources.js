define(['./module'], 
function (module) {
	
	module.factory('UserPreferencesResource', function($resource) {
	    return $resource(
	        'api/user/preferences/:id',
	        {id: '@id'},
	        {
//	            syncIntoAccount: { method :'POST',  params: {action: "sync"}, isArray : false }
	        }
	    );
	});
});