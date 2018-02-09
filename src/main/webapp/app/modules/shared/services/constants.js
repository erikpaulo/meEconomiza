define(['./module'], function (app) {

	app.service('Constants', function() {
	    return{
	        ACCOUNT: {
	            TYPE: {
	                CHECKING_ACCOUNT: {
	                    id: 'CKA',
	                    name: 'Conta Corrente'
	                },
	                CREDIT_CARD_ACCOUNT: {
	                    id: 'CCA',
	                    name: 'Cartão de Crédito'
	                },
	                INVESTMENT_ACCOUNT: {
	                    id: 'INV',
	                    name: 'Conta Investmento'
	                },
	                BENEFITS_ACCOUNT: {
	                    id: 'BFA',
	                    name: 'Conta de Benefícios'
	                },
	                STOCK_PORTFOLIO: {
	                    id: 'STK',
	                    name: 'Carteira de Ações'
	                }
	            },
	            INVESTMENT_PRODUCTS: {
	                FIXED_INCOME: {
	                    id: 'FIXED_INCOME',
	                    name: 'Renda Fixa'
	                },
	                MULTI_SHARES: {
	                    id: 'MULTI_SHARES',
	                    name: 'Multimercado'
	                },
	                FUND_OF_SHARES: {
	                    id: 'FUND_OF_SHARES',
	                    name: 'Fundo de Acões'
	                },
	                PENSION_FUND: {
	                    id: 'PENSION_FUND',
	                    name: 'Fundo de Previdência'
	                },
	                STK: {
	                    id: 'STK',
	                    name: 'Carteira de Ações'
	                },
	                OTHERS: {
	                    id: 'OTHERS',
	                    name: 'Outros'
	                }
	            },
	            LIQUIDITY_TYPE: {
	                DPLUS: {
	                    id: 'DPLUS',
	                    name: 'D+'
	                },
	                DUE_DATE: {
	                    id: 'DUE_DATE',
	                    name: 'No Vencimento'
	                }

	            },
	            INVESTMENT_RISK: {
	                HIGH: {
	                    id: 'HIGH',
	                    name: 'Alto'
	                },
	                MEDIUM: {
	                    id: 'MEDIUM',
	                    name: 'Médio'
	                },
	                LOW: {
	                    id: 'LOW',
	                    name: 'Baixo'
	                }
	            }
	        },
	        ACCOUNT_ENTRY: {
	            OPERATION: {
	                PURCHASE: {
	                    id: 'PURCHASE',
	                    name: 'Aplicação'
	                },
	                SALE: {
	                    id: 'SALE',
	                    name: 'Resgate'
	                },
	                IR_LAW: {
	                    id: 'IR_LAW',
	                    name: 'Come Cotas'
	                }
	            },
	            STOCK_OPERATION: {
	                PURCHASE: {
	                    id: 'PURCHASE',
	                    name: 'Compra'
	                },
	                SALE: {
	                    id: 'SALE',
	                    name: 'Venda'
	                }
	            }
	        },
	        GENERAL: {
	            GRAPH_COLORS: ['#263238', '#455A64', '#607D8B', '#90A4AE', '#CFD8DC', '#9E9E9E', '#E0E0E0']
	        }
	    }
	});
});