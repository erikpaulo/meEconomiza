define(['./module'], 
function (module) {
	
	module.factory('AccountResource', function($resource) {
	    return $resource(
	        'api/account/:type/:id/:action',
	        {id: '@id', type: '@type'},
	        {
	            listAll:    { method :'GET',  params: {}, isArray : true },
	            get:        { method :'GET',  params: {}, isArray : false },
	            getDetailed: { method :'GET',  params: {action: 'detail'}, isArray : false },
	            save:       { method :'POST', params: {}, isArray : false},
	            getInstitutions: { method :'GET', params: {action: 'institutions'}, isArray : true},
	            listAllForTransferable: { method :'GET', params: {action: 'transferable'}, isArray : true},
	        }
	    );
	});
});