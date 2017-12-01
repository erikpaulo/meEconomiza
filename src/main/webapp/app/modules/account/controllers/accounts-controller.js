define(['./module'
        ,'../services/account-resources'], function (app) {

	app.controller('AccountsController', ['$scope', 'AccountResource',
        function($scope, Account) {

            // Get all registered accounts;
            Account.listAll(function(accounts){
                $scope.groupedAccounts = groupAccountsByType(accounts);
            });

            function groupAccountsByType(accounts){
                var group = {};
                angular.forEach(accounts, function(account){
                    if (group[account.type] == undefined){
                        group[account.type] = [];
                    }
                    group[account.type].push(account);
                })

                return group;
            }
        }
	]);
});

