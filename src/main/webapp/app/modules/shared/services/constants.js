define(['./module'], function (app) {

	app.service('Constants', function() {
	    return{
	        ACCOUNT: {
	            TYPE: {
	                CHECKING_ACCOUNT: {
	                    id: 'CKA',
	                    name: 'Conta Corrente'
	                },
	                INVESTMENT: {
	                    id: 'INV',
	                    name: 'Conta Investmento'
	                }/*,
	                SAVING_ACCOUNT: {
	                    id: 'SVA',
	                    name: 'Conta Poupança'
	                },
	                CREDIT_ACCOUNT: {
	                    id: 'CCA',
	                    name: 'Cartão de Crédito'
	                },
	                VOUCHER_ACCOUNT: {
	                    id: 'VOA',
	                    name: 'Voucher'
	                },
	                LOAN_ACCOUNT: {
	                    id: 'LOA',
	                    name: 'Financiamento'
	                },
	                CONSUMER_GOODS_ACCOUNT: {
	                    id: 'CGA',
	                    name: 'Bens de Consumo'
	                }*/
	            }
	        }
	    }
	});
});