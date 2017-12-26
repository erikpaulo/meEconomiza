define([ './module' ], function(module) {
	module.service('CategoryConstants', function() {
	    return{
	        Subcategory: {
                Types: [
                    {id:'E', name:'Essencial'},
                    {id:'S', name:'Supérfluo'}
                ]
            },
	        Types: [
	            {id: 'EXP', name: 'Despesas'},
	            {id: 'INC', name: 'Entradas'},
	            {id: 'INV', name: 'Investimentos'}
	        ]
	    }
	});
});