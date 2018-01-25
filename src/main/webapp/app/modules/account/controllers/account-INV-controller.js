define(['./module'
        ,'../services/account-resources'
        ,'../services/account-entry-resources'
        ,'../services/index-resources'
        ,'../../shared/services/utils-service'
        ,'../../categorization/services/subcategory-resources'], function (app) {

	app.controller('AccountINVController', ['$scope', '$filter', '$mdDialog', 'AccountResource', 'AccountEntryResource', 'IndexResource', 'Utils',
        function($scope, $filter, $mdDialog, Account, AccountEntry, Index, Utils) {
            $scope.appContext.contextMenu.addAction(
                {icon: 'add', tooltip: 'Adicionar Lançamento', onClick: function() {
                    $scope.edit();
                }}
            );
            $scope.appContext.contextMenu.addAction(
                {icon: 'cached', tooltip: 'Atualizar Valor da Cota', onClick: function() {
                    $scope.updateQuoteValue();
                }}
            );

            $scope.updateQuoteValue = function(){
                $mdDialog.show({
                    controller: DialogIndexController,
                    templateUrl: 'modules/account/views/new-index-template.html',
                    parent: angular.element(document.body),
//                    locals: {
//                        indexValues: $scope.account.indexValues
//                    },
                    clickOutsideToClose:true
                }).then(function(newIndex){
                    var indexValue = new Index(newIndex);
                    indexValue.accountId = $scope.account.id
                    indexValue.$save(function(data){
                        getAccountDetail();
                        addSuccess($scope);
//                        new Account($scope.account).$getDetailed(function(data){
//                            $scope.root.account = data;
//
//                        });
                    });
                });
            }

            $scope.remove = function(entry){
                new AccountEntry(entry).$remove(function(){
                    new Account($scope.account).$getDetailed(function(data){
                        $scope.root.account = data;

                        addSuccess($scope);
                    });
                });
            }

            // Select one entry, signaling it to user
            $scope.edit = function(entry){

                $mdDialog.show({
                    controller: DialogAccountEntryController,
                    templateUrl: 'modules/account/views/new-account-INV-entry-template.html',
                    parent: angular.element(document.body),
                    locals: {
                        entry: entry
                    },
                    clickOutsideToClose:true
                }).then(function(newEntry){
                    if (newEntry){
                        var accountEntry = new AccountEntry(newEntry);
                        accountEntry.type = 'INV';
                        accountEntry.accountId = $scope.account.id;
                        accountEntry.quoteLastValue = accountEntry.quoteValue;

                        if (entry){
                            accountEntry.$save(function(e){
                                getAccountDetail()
                            }, function(err){
                                addError($scope, 'Não foi possível atualizar o lançamento.', err);
                            });
                        } else {
                            accountEntry.$save(function(e){
                                getAccountDetail();
                            }, function(err){
                                addError($scope, 'Não foi possível atualizar o lançamento.', err);
                            });
                        }
                    }
                });

            }

            function getAccountDetail(){
                new Account($scope.account).$getDetailed(function(data){
                    $scope.root.account = data;

//                    addSuccess($scope);
                });
            }

            function DialogAccountEntryController($scope, $mdDialog, Utils, Constants, entry) {
                $scope.editEntry = angular.copy(entry);
                $scope.operations = Constants.ACCOUNT_ENTRY.OPERATION;

                $scope.hide = function() {
                    $mdDialog.hide();
                };
                $scope.cancel = function() {
                    $mdDialog.cancel();
                };
                $scope.submit = function() {
                    $scope.editEntry.amount = Utils.currencyToNumber($scope.editEntry.amount);
                    $scope.editEntry.incomeTaxAmount = Utils.currencyToNumber($scope.editEntry.incomeTaxAmount);
                    $mdDialog.hide($scope.editEntry);
                };
            }

            function DialogIndexController($scope, $mdDialog/*, indexValues*/) {

                $scope.hide = function() {
                    $mdDialog.hide();
                };
                $scope.cancel = function() {
                    $mdDialog.cancel();
                };
                $scope.submit = function() {
                    $mdDialog.hide($scope.newIndex);
                };
            }
        }
	]);
});

