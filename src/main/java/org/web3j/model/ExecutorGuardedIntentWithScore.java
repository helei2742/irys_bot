package org.web3j.model;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.9.8.
 */
@SuppressWarnings("rawtypes")
public class ExecutorGuardedIntentWithScore extends Contract {
    public static final String BINARY = "6080604052348015600e575f5ffd5b505f80546001600160a01b03191633179055611c7b8061002d5f395ff3fe6080604052600436106100dc575f3560e01c80638f428af71161007c578063c885bc5811610057578063c885bc5814610258578063f13c46aa1461026c578063f233d86c14610298578063f574297a146102cc575f5ffd5b80638f428af7146101e1578063b3aea5381461020e578063c15ae3bc1461022d575f5ffd5b806351cff8d9116100b757806351cff8d9146101465780635b7bdcf9146101655780637566cc851461018c5780638da5cb5b146101ab575f5ffd5b80630db15efe146100e7578063297a91d2146101085780633c73a6f314610127575f5ffd5b366100e357005b5f5ffd5b3480156100f2575f5ffd5b506101066101013660046116d6565b610300565b005b348015610113575f5ffd5b506101066101223660046116d6565b61045e565b348015610132575f5ffd5b50610106610141366004611704565b610657565b348015610151575f5ffd5b50610106610160366004611743565b610cff565b348015610170575f5ffd5b50610179605081565b6040519081526020015b60405180910390f35b348015610197575f5ffd5b506101066101a63660046116d6565b610d7b565b3480156101b6575f5ffd5b505f546101c9906001600160a01b031681565b6040516001600160a01b039091168152602001610183565b3480156101ec575f5ffd5b506102006101fb36600461175e565b610ebd565b6040516101839291906117a4565b348015610219575f5ffd5b506101066102283660046116d6565b6110c2565b348015610238575f5ffd5b50610179610247366004611743565b60056020525f908152604090205481565b348015610263575f5ffd5b506101066112e2565b348015610277575f5ffd5b5061028b610286366004611837565b6113c0565b604051610183919061187c565b3480156102a3575f5ffd5b506101c96102b2366004611837565b60026020525f90815260409020546001600160a01b031681565b3480156102d7575f5ffd5b506101796102e6366004611743565b6001600160a01b03165f9081526007602052604090205490565b5f828152600260205260409020546001600160a01b0316331461033e5760405162461bcd60e51b81526004016103359061194c565b60405180910390fd5b60056001600160a01b0382165f90815260066020818152604080842087855290915290912060020154600160a01b900460ff169081111561038157610381611790565b036103be5760405162461bcd60e51b815260206004820152600d60248201526c30b63932b0b23c9032b93937b960991b6044820152606401610335565b5f8281526004602052604090205460ff16156104165760405162461bcd60e51b8152602060048201526017602482015276125b9d195b9d08185b1c9958591e48195e1958dd5d1959604a1b6044820152606401610335565b6001600160a01b03165f908152600660209081526040808320938352928152828220600201805460ff60a01b1916600560a01b1790556004905220805460ff19166001179055565b5f828152600260205260409020546001600160a01b031633146104935760405162461bcd60e51b81526004016103359061194c565b60016001600160a01b0382165f90815260066020818152604080842087855290915290912060020154600160a01b900460ff16908111156104d6576104d6611790565b146105125760405162461bcd60e51b815260206004820152600c60248201526b29ba30ba3ab99032b93937b960a11b6044820152606401610335565b5f61051f426103e8611988565b5f848152600160205260409020600401549091508110156105795760405162461bcd60e51b81526020600482015260146024820152730a6e8c2e4e840e8d2daca40dcdee840e4cac2c6d60631b6044820152606401610335565b5f838152600160205260409020600401546105969061ea606119a5565b811061061b576001600160a01b0382165f908152600660209081526040808320868452825291829020600201805460ff60a01b1916600160a21b179055905162461bcd60e51b81526004810191909152601e60248201527f53746172742074696d65206f7574206f6620726563656e746c792036307300006044820152606401610335565b6001600160a01b0382165f9081526006602090815260408083208684529091529020600201805460ff60a01b1916600160a11b1790555b505050565b80355f90815260016020526040902060040154156106af5760405162461bcd60e51b8152602060048201526015602482015274496e74656e7420616c72656164792065786973747360581b6044820152606401610335565b80356106eb5760405162461bcd60e51b815260206004820152600b60248201526a1a59081c995c5d5a5c995960aa1b6044820152606401610335565b60208101356107305760405162461bcd60e51b815260206004820152601160248201527019d85b59551e5c19481c995c5d5a5c9959607a1b6044820152606401610335565b604081013561077a5760405162461bcd60e51b815260206004820152601660248201527519d85b5954d95cdcda5bdb9259081c995c5d5a5c995960521b6044820152606401610335565b5f8160600135116107c65760405162461bcd60e51b815260206004820152601660248201527573636f7265206d75737420626520706f73697469766560501b6044820152606401610335565b5f81608001351161080e5760405162461bcd60e51b81526020600482015260126024820152711cdd185c9d151a5b59481c995c5d5a5c995960721b6044820152606401610335565b5f61081c60a08301836119b8565b9050116108635760405162461bcd60e51b81526020600482015260156024820152741cdd185c9d13595cdcd859d9481c995c5d5a5c9959605a1b6044820152606401610335565b5f61087160c08301836119b8565b9050116108b55760405162461bcd60e51b81526020600482015260126024820152711cdd185c9d14da59db881c995c5d5a5c995960721b6044820152606401610335565b5f8160e00135116109005760405162461bcd60e51b815260206004820152601560248201527418dbdb5c1b195d19551a5b59481c995c5d5a5c9959605a1b6044820152606401610335565b5f61090f6101008301836119b8565b90501161095e5760405162461bcd60e51b815260206004820152601860248201527f636f6d706c6574654d65737361676520726571756972656400000000000000006044820152606401610335565b5f61096d6101208301836119b8565b9050116109b45760405162461bcd60e51b815260206004820152601560248201527418dbdb5c1b195d1954da59db881c995c5d5a5c9959605a1b6044820152606401610335565b5f81610140013511610a085760405162461bcd60e51b815260206004820152601a60248201527f6c6f616453636f7265206d75737420626520706f7369746976650000000000006044820152606401610335565b60508161014001351115610a545760405162461bcd60e51b8152602060048201526013602482015272115e1958dd5d1bdc881bdd995c9b1bd8591959606a1b6044820152606401610335565b80608001358160e0013511610aba5760405162461bcd60e51b815260206004820152602660248201527f436f6d706c6574652074696d65206d7573742062652061667465722073746172604482015265742074696d6560d01b6064820152608401610335565b5f610ac860a08301836119b8565b604051610ad6929190611a02565b60405190819003902090505f610af06101008401846119b8565b604051610afe929190611a02565b60408051918290039091205f8481526003602052919091205490915060ff1615610b6a5760405162461bcd60e51b815260206004820152601960248201527f73746172744d65737361676520616c72656164792075736564000000000000006044820152606401610335565b5f8181526003602052604090205460ff1615610bc85760405162461bcd60e51b815260206004820152601c60248201527f636f6d706c6574654d65737361676520616c72656164792075736564000000006044820152606401610335565b5f8281526003602090815260408083208054600160ff1991821681179092558585528285208054909116821790558635845290915290208390610c0b8282611b62565b505060408051608081018252843581524260208201525f918101829052906060820152335f9081526006602081815260408084208835855282529283902084518155908401516001820155918301516002830180546001600160a01b03929092166001600160a01b031983168117825560608601519391926001600160a81b0319161790600160a01b908490811115610ca657610ca6611790565b021790555050335f818152600760209081526040808320805460018101825590845291832088359201829055519093507f0600186ad89c8210b98b3955c4022997214e5c605ecbadb166179b01175f08b39190a3505050565b5f546001600160a01b03163314610d455760405162461bcd60e51b815260206004820152600a60248201526927b7363c9037bbb732b960b11b6044820152606401610335565b6040516001600160a01b038216904780156108fc02915f818181858888f19350505050158015610d77573d5f5f3e3d5ffd5b5050565b5f828152600160205260408120600401549003610dd25760405162461bcd60e51b8152602060048201526015602482015274125b9d195b9d08191bd95cc81b9bdd08195e1a5cdd605a1b6044820152606401610335565b5f828152600260205260409020546001600160a01b031615610e2f5760405162461bcd60e51b8152602060048201526016602482015275125b9d195b9d08185b1c9958591e4818db185a5b595960521b6044820152606401610335565b5f8281526002602081815260408084208054336001600160a01b031990911681179091556001600160a01b03861685526006835281852087865290925280842090920180546001600160a81b03191660ff60a01b19831617600160a01b1790559051909184917f1540079d5919abeef0d73aca2fca5fad5cf4a1575095114b8405cd7b5146e1209190a35050565b6001600160a01b0383165f9081526007602052604090208054606091818510610f3857604080515f8082526020820190925290610f2f565b610f1c604080516080810182525f8082526020820181905291810182905290606082015290565b815260200190600190039081610ef55790505b509250506110ba565b5f610f4385876119a5565b905082811115610f505750815b5f610f5b8783611c1e565b90508067ffffffffffffffff811115610f7657610f76611a11565b604051908082528060200260200182016040528015610fce57816020015b610fbb604080516080810182525f8082526020820181905291810182905290606082015290565b815260200190600190039081610f945790505b5094505f5b818110156110b5576001600160a01b0389165f9081526006602052604081209085610ffe848c6119a5565b8154811061100e5761100e611c31565b5f9182526020808320909101548352828101939093526040918201902081516080810183528154815260018201549381019390935260028101546001600160a01b0381169284019290925290606083019060ff600160a01b90910416600681111561107b5761107b611790565b600681111561108c5761108c611790565b815250508682815181106110a2576110a2611c31565b6020908102919091010152600101610fd3565b505050505b935093915050565b5f8281526004602052604090205460ff161561111a5760405162461bcd60e51b8152602060048201526017602482015276125b9d195b9d08185b1c9958591e48195e1958dd5d1959604a1b6044820152606401610335565b5f828152600260205260409020546001600160a01b0316331461114f5760405162461bcd60e51b81526004016103359061194c565b60026001600160a01b0382165f90815260066020818152604080842087855290915290912060020154600160a01b900460ff169081111561119257611192611790565b146111ce5760405162461bcd60e51b815260206004820152600c60248201526b29ba30ba3ab99032b93937b960a11b6044820152606401610335565b5f6111db426103e8611988565b5f848152600160205260409020600701549091506111fb9061ea606119a5565b811061123c576001600160a01b0382165f9081526006602090815260408083208684529091529020600201805460ff60a01b1916600160a21b179055611273565b6001600160a01b0382165f9081526006602090815260408083208684529091529020600201805460ff60a01b1916600360a01b1790555b5f838152600460209081526040808320805460ff19166001908117909155338452600590925282208054919290916112ac9084906119a5565b9091555050604051339084907f7609c4682ba7f8b1ce404cb9cba36c78ce48c73460b8c8d57a1daca1842e80e8905f90a3505050565b335f90815260056020526040902054806113365760405162461bcd60e51b81526020600482015260156024820152744e6f2072657761726420746f20776974686472617760581b6044820152606401610335565b475f828210156113465781611348565b825b335f8181526005602052604080822082905551929350909183156108fc0291849190818181858888f19350505050158015611385573d5f5f3e3d5ffd5b5060405181815233907f1d3eee4ca001cff39eec6ec7615aacf2f2bd61791273830728ba00ccbd6e13379060200160405180910390a2505050565b6114166040518061016001604052805f81526020015f81526020015f81526020015f81526020015f815260200160608152602001606081526020015f815260200160608152602001606081526020015f81525090565b60015f8381526020019081526020015f20604051806101600160405290815f82015481526020016001820154815260200160028201548152602001600382015481526020016004820154815260200160058201805461147490611a25565b80601f01602080910402602001604051908101604052809291908181526020018280546114a090611a25565b80156114eb5780601f106114c2576101008083540402835291602001916114eb565b820191905f5260205f20905b8154815290600101906020018083116114ce57829003601f168201915b5050505050815260200160068201805461150490611a25565b80601f016020809104026020016040519081016040528092919081815260200182805461153090611a25565b801561157b5780601f106115525761010080835404028352916020019161157b565b820191905f5260205f20905b81548152906001019060200180831161155e57829003601f168201915b505050505081526020016007820154815260200160088201805461159e90611a25565b80601f01602080910402602001604051908101604052809291908181526020018280546115ca90611a25565b80156116155780601f106115ec57610100808354040283529160200191611615565b820191905f5260205f20905b8154815290600101906020018083116115f857829003601f168201915b5050505050815260200160098201805461162e90611a25565b80601f016020809104026020016040519081016040528092919081815260200182805461165a90611a25565b80156116a55780601f1061167c576101008083540402835291602001916116a5565b820191905f5260205f20905b81548152906001019060200180831161168857829003601f168201915b50505050508152602001600a820154815250509050919050565b6001600160a01b03811681146116d3575f5ffd5b50565b5f5f604083850312156116e7575f5ffd5b8235915060208301356116f9816116bf565b809150509250929050565b5f60208284031215611714575f5ffd5b813567ffffffffffffffff81111561172a575f5ffd5b8201610160818503121561173c575f5ffd5b9392505050565b5f60208284031215611753575f5ffd5b813561173c816116bf565b5f5f5f60608486031215611770575f5ffd5b833561177b816116bf565b95602085013595506040909401359392505050565b634e487b7160e01b5f52602160045260245ffd5b604080825283519082018190525f9060208501906060840190835b8181101561182457835180518452602080820151908501526040808201516001600160a01b031690850152606001516007811061180a57634e487b7160e01b5f52602160045260245ffd5b6060840152602093909301926080909201916001016117bf565b5050602093909301939093525092915050565b5f60208284031215611847575f5ffd5b5035919050565b5f81518084528060208401602086015e5f602082860101526020601f19601f83011685010191505092915050565b6020815281516020820152602082015160408201526040820151606082015260608201516080820152608082015160a08201525f60a083015161016060c08401526118cb61018084018261184e565b905060c0840151601f198483030160e08501526118e8828261184e565b91505060e0840151610100840152610100840151601f1984830301610120850152611913828261184e565b915050610120840151601f1984830301610140850152611933828261184e565b9150506101408401516101608401528091505092915050565b6020808252600e908201526d139bdd08185d5d1a1bdc9a5e995960921b604082015260600190565b634e487b7160e01b5f52601160045260245ffd5b808202811582820484141761199f5761199f611974565b92915050565b8082018082111561199f5761199f611974565b5f5f8335601e198436030181126119cd575f5ffd5b83018035915067ffffffffffffffff8211156119e7575f5ffd5b6020019150368190038213156119fb575f5ffd5b9250929050565b818382375f9101908152919050565b634e487b7160e01b5f52604160045260245ffd5b600181811c90821680611a3957607f821691505b602082108103611a5757634e487b7160e01b5f52602260045260245ffd5b50919050565b601f82111561065257805f5260205f20601f840160051c81016020851015611a825750805b601f840160051c820191505b81811015611aa1575f8155600101611a8e565b5050505050565b67ffffffffffffffff831115611ac057611ac0611a11565b611ad483611ace8354611a25565b83611a5d565b5f601f841160018114611b05575f8515611aee5750838201355b5f19600387901b1c1916600186901b178355611aa1565b5f83815260208120601f198716915b82811015611b345786850135825560209485019460019092019101611b14565b5086821015611b50575f1960f88860031b161c19848701351681555b505060018560011b0183555050505050565b8135815560208201356001820155604082013560028201556060820135600382015560808201356004820155611b9b60a08301836119b8565b611ba9818360058601611aa8565b5050611bb860c08301836119b8565b611bc6818360068601611aa8565b505060e08201356007820155611be06101008301836119b8565b611bee818360088601611aa8565b5050611bfe6101208301836119b8565b611c0c818360098601611aa8565b50506101409190910135600a90910155565b8181038181111561199f5761199f611974565b634e487b7160e01b5f52603260045260245ffdfea26469706673582212202afd15af605eb154a38b579500d5cc85a49e85595ed717113e200f23b18b906164736f6c634300081d0033";

    public static final String FUNC_MAX_LOAD = "MAX_LOAD";

    public static final String FUNC_CLAIMINTENT = "claimIntent";

    public static final String FUNC_COMMITERROR = "commitError";

    public static final String FUNC_COMPLETEEXECUTEINTENT = "completeExecuteIntent";

    public static final String FUNC_EXECUTIONCOUNT = "executionCount";

    public static final String FUNC_EXECUTOR = "executor";

    public static final String FUNC_GETINTENT = "getIntent";

    public static final String FUNC_GETUSERINTENTCOUNT = "getUserIntentCount";

    public static final String FUNC_GETUSERINTENTSPAGED = "getUserIntentsPaged";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_REGISTERINTENT = "registerIntent";

    public static final String FUNC_STARTEXECUTEINTENT = "startExecuteIntent";

    public static final String FUNC_WITHDRAW = "withdraw";

    public static final String FUNC_WITHDRAWREWARD = "withdrawReward";

    public static final Event INTENTCLAIMED_EVENT = new Event("IntentClaimed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event INTENTEXECUTED_EVENT = new Event("IntentExecuted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event INTENTREGISTERED_EVENT = new Event("IntentRegistered", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Bytes32>(true) {}));
    ;

    public static final Event REWARDWITHDRAWN_EVENT = new Event("RewardWithdrawn", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected ExecutorGuardedIntentWithScore(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected ExecutorGuardedIntentWithScore(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected ExecutorGuardedIntentWithScore(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected ExecutorGuardedIntentWithScore(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<IntentClaimedEventResponse> getIntentClaimedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(INTENTCLAIMED_EVENT, transactionReceipt);
        ArrayList<IntentClaimedEventResponse> responses = new ArrayList<IntentClaimedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            IntentClaimedEventResponse typedResponse = new IntentClaimedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.intentId = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.executor = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static IntentClaimedEventResponse getIntentClaimedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(INTENTCLAIMED_EVENT, log);
        IntentClaimedEventResponse typedResponse = new IntentClaimedEventResponse();
        typedResponse.log = log;
        typedResponse.intentId = (byte[]) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.executor = (String) eventValues.getIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<IntentClaimedEventResponse> intentClaimedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getIntentClaimedEventFromLog(log));
    }

    public Flowable<IntentClaimedEventResponse> intentClaimedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(INTENTCLAIMED_EVENT));
        return intentClaimedEventFlowable(filter);
    }

    public static List<IntentExecutedEventResponse> getIntentExecutedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(INTENTEXECUTED_EVENT, transactionReceipt);
        ArrayList<IntentExecutedEventResponse> responses = new ArrayList<IntentExecutedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            IntentExecutedEventResponse typedResponse = new IntentExecutedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.intentId = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.executor = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static IntentExecutedEventResponse getIntentExecutedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(INTENTEXECUTED_EVENT, log);
        IntentExecutedEventResponse typedResponse = new IntentExecutedEventResponse();
        typedResponse.log = log;
        typedResponse.intentId = (byte[]) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.executor = (String) eventValues.getIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<IntentExecutedEventResponse> intentExecutedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getIntentExecutedEventFromLog(log));
    }

    public Flowable<IntentExecutedEventResponse> intentExecutedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(INTENTEXECUTED_EVENT));
        return intentExecutedEventFlowable(filter);
    }

    public static List<IntentRegisteredEventResponse> getIntentRegisteredEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(INTENTREGISTERED_EVENT, transactionReceipt);
        ArrayList<IntentRegisteredEventResponse> responses = new ArrayList<IntentRegisteredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            IntentRegisteredEventResponse typedResponse = new IntentRegisteredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.user = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.intentId = (byte[]) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static IntentRegisteredEventResponse getIntentRegisteredEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(INTENTREGISTERED_EVENT, log);
        IntentRegisteredEventResponse typedResponse = new IntentRegisteredEventResponse();
        typedResponse.log = log;
        typedResponse.user = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.intentId = (byte[]) eventValues.getIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<IntentRegisteredEventResponse> intentRegisteredEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getIntentRegisteredEventFromLog(log));
    }

    public Flowable<IntentRegisteredEventResponse> intentRegisteredEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(INTENTREGISTERED_EVENT));
        return intentRegisteredEventFlowable(filter);
    }

    public static List<RewardWithdrawnEventResponse> getRewardWithdrawnEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(REWARDWITHDRAWN_EVENT, transactionReceipt);
        ArrayList<RewardWithdrawnEventResponse> responses = new ArrayList<RewardWithdrawnEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RewardWithdrawnEventResponse typedResponse = new RewardWithdrawnEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.executor = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static RewardWithdrawnEventResponse getRewardWithdrawnEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(REWARDWITHDRAWN_EVENT, log);
        RewardWithdrawnEventResponse typedResponse = new RewardWithdrawnEventResponse();
        typedResponse.log = log;
        typedResponse.executor = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<RewardWithdrawnEventResponse> rewardWithdrawnEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getRewardWithdrawnEventFromLog(log));
    }

    public Flowable<RewardWithdrawnEventResponse> rewardWithdrawnEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REWARDWITHDRAWN_EVENT));
        return rewardWithdrawnEventFlowable(filter);
    }

    public RemoteFunctionCall<BigInteger> MAX_LOAD() {
        final Function function = new Function(FUNC_MAX_LOAD, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> claimIntent(byte[] intentId, String target) {
        final Function function = new Function(
                FUNC_CLAIMINTENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(intentId), 
                new org.web3j.abi.datatypes.Address(160, target)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> commitError(byte[] intentId, String target) {
        final Function function = new Function(
                FUNC_COMMITERROR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(intentId), 
                new org.web3j.abi.datatypes.Address(160, target)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> completeExecuteIntent(byte[] intentId, String target) {
        final Function function = new Function(
                FUNC_COMPLETEEXECUTEINTENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(intentId), 
                new org.web3j.abi.datatypes.Address(160, target)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> executionCount(String param0) {
        final Function function = new Function(FUNC_EXECUTIONCOUNT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> executor(byte[] param0) {
        final Function function = new Function(FUNC_EXECUTOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<IntentData> getIntent(byte[] intentId) {
        final Function function = new Function(FUNC_GETINTENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(intentId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<IntentData>() {}));
        return executeRemoteCallSingleValueReturn(function, IntentData.class);
    }

    public RemoteFunctionCall<BigInteger> getUserIntentCount(String user) {
        final Function function = new Function(FUNC_GETUSERINTENTCOUNT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, user)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple2<List<UserIntent>, BigInteger>> getUserIntentsPaged(String user, BigInteger offset, BigInteger limit) {
        final Function function = new Function(FUNC_GETUSERINTENTSPAGED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, user), 
                new org.web3j.abi.datatypes.generated.Uint256(offset), 
                new org.web3j.abi.datatypes.generated.Uint256(limit)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<UserIntent>>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple2<List<UserIntent>, BigInteger>>(function,
                new Callable<Tuple2<List<UserIntent>, BigInteger>>() {
                    @Override
                    public Tuple2<List<UserIntent>, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<List<UserIntent>, BigInteger>(
                                convertToNative((List<UserIntent>) results.get(0).getValue()), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> registerIntent(IntentData data) {
        final Function function = new Function(
                FUNC_REGISTERINTENT, 
                Arrays.<Type>asList(data), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> startExecuteIntent(byte[] intentId, String target) {
        final Function function = new Function(
                FUNC_STARTEXECUTEINTENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(intentId), 
                new org.web3j.abi.datatypes.Address(160, target)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> withdraw(String to) {
        final Function function = new Function(
                FUNC_WITHDRAW, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, to)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> withdrawReward() {
        final Function function = new Function(
                FUNC_WITHDRAWREWARD, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static ExecutorGuardedIntentWithScore load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new ExecutorGuardedIntentWithScore(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static ExecutorGuardedIntentWithScore load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new ExecutorGuardedIntentWithScore(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static ExecutorGuardedIntentWithScore load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new ExecutorGuardedIntentWithScore(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static ExecutorGuardedIntentWithScore load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new ExecutorGuardedIntentWithScore(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<ExecutorGuardedIntentWithScore> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(ExecutorGuardedIntentWithScore.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<ExecutorGuardedIntentWithScore> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(ExecutorGuardedIntentWithScore.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<ExecutorGuardedIntentWithScore> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(ExecutorGuardedIntentWithScore.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<ExecutorGuardedIntentWithScore> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(ExecutorGuardedIntentWithScore.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class IntentData extends DynamicStruct {
        public byte[] id;

        public byte[] gameType;

        public byte[] gameSessionId;

        public BigInteger score;

        public BigInteger startTime;

        public byte[] startMessage;

        public byte[] startSign;

        public BigInteger completeTime;

        public byte[] completeMessage;

        public byte[] completeSign;

        public BigInteger loadScore;

        public IntentData(byte[] id, byte[] gameType, byte[] gameSessionId, BigInteger score, BigInteger startTime, byte[] startMessage, byte[] startSign, BigInteger completeTime, byte[] completeMessage, byte[] completeSign, BigInteger loadScore) {
            super(new org.web3j.abi.datatypes.generated.Bytes32(id), 
                    new org.web3j.abi.datatypes.generated.Bytes32(gameType), 
                    new org.web3j.abi.datatypes.generated.Bytes32(gameSessionId), 
                    new org.web3j.abi.datatypes.generated.Uint256(score), 
                    new org.web3j.abi.datatypes.generated.Uint256(startTime), 
                    new org.web3j.abi.datatypes.DynamicBytes(startMessage), 
                    new org.web3j.abi.datatypes.DynamicBytes(startSign), 
                    new org.web3j.abi.datatypes.generated.Uint256(completeTime), 
                    new org.web3j.abi.datatypes.DynamicBytes(completeMessage), 
                    new org.web3j.abi.datatypes.DynamicBytes(completeSign), 
                    new org.web3j.abi.datatypes.generated.Uint256(loadScore));
            this.id = id;
            this.gameType = gameType;
            this.gameSessionId = gameSessionId;
            this.score = score;
            this.startTime = startTime;
            this.startMessage = startMessage;
            this.startSign = startSign;
            this.completeTime = completeTime;
            this.completeMessage = completeMessage;
            this.completeSign = completeSign;
            this.loadScore = loadScore;
        }

        public IntentData(Bytes32 id, Bytes32 gameType, Bytes32 gameSessionId, Uint256 score, Uint256 startTime, DynamicBytes startMessage, DynamicBytes startSign, Uint256 completeTime, DynamicBytes completeMessage, DynamicBytes completeSign, Uint256 loadScore) {
            super(id, gameType, gameSessionId, score, startTime, startMessage, startSign, completeTime, completeMessage, completeSign, loadScore);
            this.id = id.getValue();
            this.gameType = gameType.getValue();
            this.gameSessionId = gameSessionId.getValue();
            this.score = score.getValue();
            this.startTime = startTime.getValue();
            this.startMessage = startMessage.getValue();
            this.startSign = startSign.getValue();
            this.completeTime = completeTime.getValue();
            this.completeMessage = completeMessage.getValue();
            this.completeSign = completeSign.getValue();
            this.loadScore = loadScore.getValue();
        }
    }

    public static class UserIntent extends StaticStruct {
        public byte[] id;

        public BigInteger submitTime;

        public String executor;

        public BigInteger status;

        public UserIntent(byte[] id, BigInteger submitTime, String executor, BigInteger status) {
            super(new org.web3j.abi.datatypes.generated.Bytes32(id), 
                    new org.web3j.abi.datatypes.generated.Uint256(submitTime), 
                    new org.web3j.abi.datatypes.Address(160, executor), 
                    new org.web3j.abi.datatypes.generated.Uint8(status));
            this.id = id;
            this.submitTime = submitTime;
            this.executor = executor;
            this.status = status;
        }

        public UserIntent(Bytes32 id, Uint256 submitTime, Address executor, Uint8 status) {
            super(id, submitTime, executor, status);
            this.id = id.getValue();
            this.submitTime = submitTime.getValue();
            this.executor = executor.getValue();
            this.status = status.getValue();
        }
    }

    public static class IntentClaimedEventResponse extends BaseEventResponse {
        public byte[] intentId;

        public String executor;
    }

    public static class IntentExecutedEventResponse extends BaseEventResponse {
        public byte[] intentId;

        public String executor;
    }

    public static class IntentRegisteredEventResponse extends BaseEventResponse {
        public String user;

        public byte[] intentId;
    }

    public static class RewardWithdrawnEventResponse extends BaseEventResponse {
        public String executor;

        public BigInteger amount;
    }
}
