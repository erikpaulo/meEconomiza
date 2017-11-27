define([ './module' ], function(module) {
	module.service('CategoryConstants', function() {
	    return{
	        Subcategory: {
                Types: [
                    {id:'F', name:'Mensal Fixo'},
                    {id:'I', name:'Mensal Irregular'},
                    {id:'V', name:'Variável'}
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