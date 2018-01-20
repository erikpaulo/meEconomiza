define(['./module'
        ,'../services/account-resources'
        ,'../services/account-entry-resources'
        ,'../../shared/services/utils-service'
        ,'../../categorization/services/subcategory-resources'], function (app) {

	app.controller('AccountCKAController', ['$scope', '$filter', '$mdDialog', 'AccountResource', 'AccountEntryResource', 'SubCategoryResource', 'Utils',
        function($scope, $filter, $mdDialog, Account, AccountEntry, SubCategory, Constants, Utils) {
            $scope.appContext.contextMenu.addAction(
                {icon: 'add', tooltip: 'Adicionar Lançamento', onClick: function() {
                    $scope.edit();
                }}
            );

            $scope.accountsToTransfer;
            Account.listAllForTransferable(function (data){
                $scope.accountsToTransfer = data;
            });

            SubCategory.listAll(function (subCategories){
                $scope.subCategories = subCategories;
            });

            // Select one entry, signaling it to user
            $scope.edit = function(entry){

                $mdDialog.show({
                    controller: DialogController,
                    templateUrl: 'modules/account/views/new-account-CKA-entry-template.html',
                    parent: angular.element(document.body),
                    locals: {
                        subCategories: $scope.subCategories,
                        entry: entry,
                        accountsToTransfer: $scope.accountsToTransfer,
                        account: $scope.account
                    },
                    clickOutsideToClose:true
                }).then(function(newEntry){
                    if (newEntry){
                        var accountEntry = new AccountEntry(newEntry);
                        accountEntry.type = 'CKA';
                        accountEntry.accountId = $scope.account.id;

                        if (entry){
                            accountEntry.$save(function(e){
                                for (var i in $scope.account.entries){
                                    if (e.id == $scope.account.entries[i].id) {
                                        $scope.account.entries[i] = e;
                                        break;
                                    }
                                }
                                updateView();

                                addSuccess($scope);
                            }, function(err){
                                addError($scope, 'Não foi possível atualizar o lançamento.', err);
                            });
                        } else {
                            accountEntry.$save(function(e){
                                $scope.account.entries.push(e);
                                updateView();

                                addSuccess($scope);
                            }, function(err){
                                addError($scope, 'Não foi possível atualizar o lançamento.', err);
                            });
                        }
                    }
                });

            }

            $scope.remove = function(entry){
                var accountEntry = new AccountEntry(entry);
                accountEntry.type = 'CKA';
                accountEntry.$remove(function(){
                    for (var i in $scope.account.entries){
                        if($scope.account.entries[i].id == entry.id){
                            $scope.account.entries.splice(i, 1);
                        }
                    }
                    updateView();

                    addSuccess($scope);
                }, function(err){
                    addError($scope, err.data.message, err);
                })
            }

            function updateView(entry, newEntry){
                $scope.account.entries = $filter('orderBy')($scope.account.entries, 'date', false);

                var balance = $scope.account.startBalance;
                for (var i in $scope.account.entries){
                    balance += $scope.account.entries[i].amount;
                    $scope.account.entries[i].balance = balance;
                }
                $scope.account.balance = balance;
            }

            function DialogController($scope, $mdDialog, $filter, Utils, subCategories, entry, accountsToTransfer, account) {
                if (!entry) entry = {transfer: false}

                $scope.editEntry = angular.copy(entry);
                $scope.subCategories = subCategories;
                $scope.accountsToTransfer = accountsToTransfer;
                for (var i in accountsToTransfer){
                    if (accountsToTransfer[i].id == account.id){
                        accountsToTransfer.splice(i, 1);
                    }
                }

                $scope.querySearch = function(query){
                    return $filter('filter')($scope.subCategories, query);
                }

                $scope.hide = function() {
                    $mdDialog.hide();
                };
                $scope.cancel = function() {
                    $mdDialog.cancel();
                };
                $scope.submit = function() {
                    $scope.editEntry.amount = Utils.currencyToNumber($scope.editEntry.amount);
                    $mdDialog.hide($scope.editEntry);
                };
            }
        }
	]);
});

