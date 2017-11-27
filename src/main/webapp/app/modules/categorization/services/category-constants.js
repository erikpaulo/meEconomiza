define([ './module' ], function(module) {
	module.service('CategoryConstants', function() {
	    return{
	        Types: [
	            {id:'F', name:'Mensal Fixo'},
	            {id:'I', name:'Mensal Irregular'},
	            {id:'V', name:'Variável'}
	        ],
	        Kinds: [
	            {id: 'EXP', name: 'Despesa'},
	            {id: 'INC', name: 'Entrada'},
	            {id: 'INV', name: 'Investimento'}
	        ]
	    }
	});
});