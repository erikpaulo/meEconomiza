define(['./module'], function (app) {

    app.filter('accountTypeName', function () {
        var typeNameHash = {
            CKA: 'Conta Corrente',
            SVA: 'Conta Poupança',
            INV: 'Conta Investimento',
            CCA: 'Cartão de Crédito',
            LOA: 'Financiamento',
            STK: 'Carteira de Ações',
            BFA: 'Conta de Benefícios'
        }
        return function (input) {
            return (typeNameHash[input] ? typeNameHash[input] : 'Not Found');
        };
    });

    app.filter('investTypeName', function () {
        var typeNameHash = {
            FIXED_INCOME: 'Renda Fixa',
            FUND_OF_SHARES: 'Fundo de Acões',
            PENSION_FUND: 'Fundo de Previdência',
            STK: 'Carteira de Ações',
            OTHERS: 'Outros'
        }
        return function (input) {
            return (typeNameHash[input] ? typeNameHash[input] : 'Not Found');
        };
    });
});