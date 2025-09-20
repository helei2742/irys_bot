package cn.com.vortexa.irys_onchain_bot.onchain;

import io.reactivex.Flowable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
public class ExecutorGuardedIntent extends Contract {
    public static final String BINARY = "6080604052348015600e575f5ffd5b505f80546001600160a01b031916331790556112c88061002d5f395ff3fe608060405260043610610092575f3560e01c80638da5cb5b116100575780638da5cb5b14610142578063c15ae3bc14610178578063c885bc58146101a3578063f13c46aa146101b7578063f233d86c146101e3575f5ffd5b80633c73a6f31461009d57806340e624cb146100be57806351cff8d9146100dd5780635b7bdcf9146100fc5780637a1c9ca714610123575f5ffd5b3661009957005b5f5ffd5b3480156100a8575f5ffd5b506100bc6100b7366004610e80565b610217565b005b3480156100c9575f5ffd5b506100bc6100d8366004610ebf565b610804565b3480156100e8575f5ffd5b506100bc6100f7366004610eed565b610906565b348015610107575f5ffd5b50610110605081565b6040519081526020015b60405180910390f35b34801561012e575f5ffd5b506100bc61013d366004610ebf565b610982565b34801561014d575f5ffd5b505f54610160906001600160a01b031681565b6040516001600160a01b03909116815260200161011a565b348015610183575f5ffd5b50610110610192366004610eed565b60056020525f908152604090205481565b3480156101ae575f5ffd5b506100bc610aa3565b3480156101c2575f5ffd5b506101d66101d1366004610ebf565b610b81565b60405161011a9190610f36565b3480156101ee575f5ffd5b506101606101fd366004610ebf565b60026020525f90815260409020546001600160a01b031681565b80355f90815260016020526040902060040154156102745760405162461bcd60e51b8152602060048201526015602482015274496e74656e7420616c72656164792065786973747360581b60448201526064015b60405180910390fd5b80356102b05760405162461bcd60e51b815260206004820152600b60248201526a1a59081c995c5d5a5c995960aa1b604482015260640161026b565b60208101356102f55760405162461bcd60e51b815260206004820152601160248201527019d85b59551e5c19481c995c5d5a5c9959607a1b604482015260640161026b565b604081013561033f5760405162461bcd60e51b815260206004820152601660248201527519d85b5954d95cdcda5bdb9259081c995c5d5a5c995960521b604482015260640161026b565b5f81606001351161038b5760405162461bcd60e51b815260206004820152601660248201527573636f7265206d75737420626520706f73697469766560501b604482015260640161026b565b5f8160800135116103d35760405162461bcd60e51b81526020600482015260126024820152711cdd185c9d151a5b59481c995c5d5a5c995960721b604482015260640161026b565b5f6103e160a0830183611006565b9050116104285760405162461bcd60e51b81526020600482015260156024820152741cdd185c9d13595cdcd859d9481c995c5d5a5c9959605a1b604482015260640161026b565b5f61043660c0830183611006565b90501161047a5760405162461bcd60e51b81526020600482015260126024820152711cdd185c9d14da59db881c995c5d5a5c995960721b604482015260640161026b565b5f8160e00135116104c55760405162461bcd60e51b815260206004820152601560248201527418dbdb5c1b195d19551a5b59481c995c5d5a5c9959605a1b604482015260640161026b565b5f6104d4610100830183611006565b9050116105235760405162461bcd60e51b815260206004820152601860248201527f636f6d706c6574654d6573736167652072657175697265640000000000000000604482015260640161026b565b5f610532610120830183611006565b9050116105795760405162461bcd60e51b815260206004820152601560248201527418dbdb5c1b195d1954da59db881c995c5d5a5c9959605a1b604482015260640161026b565b5f816101400135116105cd5760405162461bcd60e51b815260206004820152601a60248201527f6c6f616453636f7265206d75737420626520706f736974697665000000000000604482015260640161026b565b605081610140013511156106195760405162461bcd60e51b8152602060048201526013602482015272115e1958dd5d1bdc881bdd995c9b1bd8591959606a1b604482015260640161026b565b80608001358160e001351161067f5760405162461bcd60e51b815260206004820152602660248201527f436f6d706c6574652074696d65206d7573742062652061667465722073746172604482015265742074696d6560d01b606482015260840161026b565b5f61068d60a0830183611006565b60405161069b929190611050565b60405190819003902090505f6106b5610100840184611006565b6040516106c3929190611050565b60408051918290039091205f8481526003602052919091205490915060ff161561072f5760405162461bcd60e51b815260206004820152601960248201527f73746172744d65737361676520616c7265616479207573656400000000000000604482015260640161026b565b5f8181526003602052604090205460ff161561078d5760405162461bcd60e51b815260206004820152601c60248201527f636f6d706c6574654d65737361676520616c7265616479207573656400000000604482015260640161026b565b5f8281526003602090815260408083208054600160ff19918216811790925585855282852080549091168217905586358452909152902083906107d082826111b1565b505060405183359033907f0600186ad89c8210b98b3955c4022997214e5c605ecbadb166179b01175f08b3905f90a3505050565b5f81815260016020526040812060040154900361085b5760405162461bcd60e51b8152602060048201526015602482015274125b9d195b9d08191bd95cc81b9bdd08195e1a5cdd605a1b604482015260640161026b565b5f818152600260205260409020546001600160a01b0316156108b85760405162461bcd60e51b8152602060048201526016602482015275125b9d195b9d08185b1c9958591e4818db185a5b595960521b604482015260640161026b565b5f8181526002602052604080822080546001600160a01b031916339081179091559051909183917f1540079d5919abeef0d73aca2fca5fad5cf4a1575095114b8405cd7b5146e1209190a350565b5f546001600160a01b0316331461094c5760405162461bcd60e51b815260206004820152600a60248201526927b7363c9037bbb732b960b11b604482015260640161026b565b6040516001600160a01b038216904780156108fc02915f818181858888f1935050505015801561097e573d5f5f3e3d5ffd5b5050565b5f818152600260205260409020546001600160a01b031633146109d85760405162461bcd60e51b815260206004820152600e60248201526d139bdd08185d5d1a1bdc9a5e995960921b604482015260640161026b565b5f8181526004602052604090205460ff1615610a365760405162461bcd60e51b815260206004820152601760248201527f496e74656e7420616c7265616479206578656375746564000000000000000000604482015260640161026b565b5f818152600460209081526040808320805460ff1916600190811790915533845260059092528220805491929091610a6f90849061126d565b9091555050604051339082907f7609c4682ba7f8b1ce404cb9cba36c78ce48c73460b8c8d57a1daca1842e80e8905f90a350565b335f9081526005602052604090205480610af75760405162461bcd60e51b81526020600482015260156024820152744e6f2072657761726420746f20776974686472617760581b604482015260640161026b565b475f82821015610b075781610b09565b825b335f8181526005602052604080822082905551929350909183156108fc0291849190818181858888f19350505050158015610b46573d5f5f3e3d5ffd5b5060405181815233907f1d3eee4ca001cff39eec6ec7615aacf2f2bd61791273830728ba00ccbd6e13379060200160405180910390a2505050565b610bd76040518061016001604052805f81526020015f81526020015f81526020015f81526020015f815260200160608152602001606081526020015f815260200160608152602001606081526020015f81525090565b60015f8381526020019081526020015f20604051806101600160405290815f820154815260200160018201548152602001600282015481526020016003820154815260200160048201548152602001600582018054610c3590611073565b80601f0160208091040260200160405190810160405280929190818152602001828054610c6190611073565b8015610cac5780601f10610c8357610100808354040283529160200191610cac565b820191905f5260205f20905b815481529060010190602001808311610c8f57829003601f168201915b50505050508152602001600682018054610cc590611073565b80601f0160208091040260200160405190810160405280929190818152602001828054610cf190611073565b8015610d3c5780601f10610d1357610100808354040283529160200191610d3c565b820191905f5260205f20905b815481529060010190602001808311610d1f57829003601f168201915b5050505050815260200160078201548152602001600882018054610d5f90611073565b80601f0160208091040260200160405190810160405280929190818152602001828054610d8b90611073565b8015610dd65780601f10610dad57610100808354040283529160200191610dd6565b820191905f5260205f20905b815481529060010190602001808311610db957829003601f168201915b50505050508152602001600982018054610def90611073565b80601f0160208091040260200160405190810160405280929190818152602001828054610e1b90611073565b8015610e665780601f10610e3d57610100808354040283529160200191610e66565b820191905f5260205f20905b815481529060010190602001808311610e4957829003601f168201915b50505050508152602001600a820154815250509050919050565b5f60208284031215610e90575f5ffd5b813567ffffffffffffffff811115610ea6575f5ffd5b82016101608185031215610eb8575f5ffd5b9392505050565b5f60208284031215610ecf575f5ffd5b5035919050565b6001600160a01b0381168114610eea575f5ffd5b50565b5f60208284031215610efd575f5ffd5b8135610eb881610ed6565b5f81518084528060208401602086015e5f602082860101526020601f19601f83011685010191505092915050565b6020815281516020820152602082015160408201526040820151606082015260608201516080820152608082015160a08201525f60a083015161016060c0840152610f85610180840182610f08565b905060c0840151601f198483030160e0850152610fa28282610f08565b91505060e0840151610100840152610100840151601f1984830301610120850152610fcd8282610f08565b915050610120840151601f1984830301610140850152610fed8282610f08565b9150506101408401516101608401528091505092915050565b5f5f8335601e1984360301811261101b575f5ffd5b83018035915067ffffffffffffffff821115611035575f5ffd5b602001915036819003821315611049575f5ffd5b9250929050565b818382375f9101908152919050565b634e487b7160e01b5f52604160045260245ffd5b600181811c9082168061108757607f821691505b6020821081036110a557634e487b7160e01b5f52602260045260245ffd5b50919050565b601f8211156110f257805f5260205f20601f840160051c810160208510156110d05750805b601f840160051c820191505b818110156110ef575f81556001016110dc565b50505b505050565b67ffffffffffffffff83111561110f5761110f61105f565b6111238361111d8354611073565b836110ab565b5f601f841160018114611154575f851561113d5750838201355b5f19600387901b1c1916600186901b1783556110ef565b5f83815260208120601f198716915b828110156111835786850135825560209485019460019092019101611163565b508682101561119f575f1960f88860031b161c19848701351681555b505060018560011b0183555050505050565b81358155602082013560018201556040820135600282015560608201356003820155608082013560048201556111ea60a0830183611006565b6111f88183600586016110f7565b505061120760c0830183611006565b6112158183600686016110f7565b505060e0820135600782015561122f610100830183611006565b61123d8183600886016110f7565b505061124d610120830183611006565b61125b8183600986016110f7565b50506101409190910135600a90910155565b8082018082111561128c57634e487b7160e01b5f52601160045260245ffd5b9291505056fea2646970667358221220cff06f2fcf52e2c3e96e19a1db49555d6303a8d235dd6e696670cb2f3fc900a164736f6c634300081d0033";

    public static final String FUNC_MAX_LOAD = "MAX_LOAD";

    public static final String FUNC_CLAIMINTENT = "claimIntent";

    public static final String FUNC_EXECUTEINTENT = "executeIntent";

    public static final String FUNC_EXECUTIONCOUNT = "executionCount";

    public static final String FUNC_EXECUTOR = "executor";

    public static final String FUNC_GETINTENT = "getIntent";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_REGISTERINTENT = "registerIntent";

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
    protected ExecutorGuardedIntent(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected ExecutorGuardedIntent(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected ExecutorGuardedIntent(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected ExecutorGuardedIntent(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<IntentClaimedEventResponse> getIntentClaimedEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(INTENTCLAIMED_EVENT, transactionReceipt);
        ArrayList<IntentClaimedEventResponse> responses = new ArrayList<IntentClaimedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            IntentClaimedEventResponse typedResponse = new IntentClaimedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.intentId = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.executor = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static IntentClaimedEventResponse getIntentClaimedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(INTENTCLAIMED_EVENT, log);
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
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(INTENTEXECUTED_EVENT, transactionReceipt);
        ArrayList<IntentExecutedEventResponse> responses = new ArrayList<IntentExecutedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            IntentExecutedEventResponse typedResponse = new IntentExecutedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.intentId = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.executor = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static IntentExecutedEventResponse getIntentExecutedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(INTENTEXECUTED_EVENT, log);
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
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(INTENTREGISTERED_EVENT, transactionReceipt);
        ArrayList<IntentRegisteredEventResponse> responses = new ArrayList<IntentRegisteredEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            IntentRegisteredEventResponse typedResponse = new IntentRegisteredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.user = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.intentId = (byte[]) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static IntentRegisteredEventResponse getIntentRegisteredEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(INTENTREGISTERED_EVENT, log);
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
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(REWARDWITHDRAWN_EVENT, transactionReceipt);
        ArrayList<RewardWithdrawnEventResponse> responses = new ArrayList<RewardWithdrawnEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            RewardWithdrawnEventResponse typedResponse = new RewardWithdrawnEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.executor = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static RewardWithdrawnEventResponse getRewardWithdrawnEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(REWARDWITHDRAWN_EVENT, log);
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

    public RemoteFunctionCall<TransactionReceipt> claimIntent(byte[] intentId) {
        final Function function = new Function(
                FUNC_CLAIMINTENT,
                Arrays.<Type>asList(new Bytes32(intentId)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> executeIntent(byte[] intentId) {
        final Function function = new Function(
                FUNC_EXECUTEINTENT,
                Arrays.<Type>asList(new Bytes32(intentId)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> executionCount(String param0) {
        final Function function = new Function(FUNC_EXECUTIONCOUNT,
                Arrays.<Type>asList(new Address(160, param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> executor(byte[] param0) {
        final Function function = new Function(FUNC_EXECUTOR,
                Arrays.<Type>asList(new Bytes32(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<IntentData> getIntent(byte[] intentId) {
        final Function function = new Function(FUNC_GETINTENT,
                Arrays.<Type>asList(new Bytes32(intentId)),
                Arrays.<TypeReference<?>>asList(new TypeReference<IntentData>() {}));
        return executeRemoteCallSingleValueReturn(function, IntentData.class);
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

    public RemoteFunctionCall<TransactionReceipt> withdraw(String to) {
        final Function function = new Function(
                FUNC_WITHDRAW,
                Arrays.<Type>asList(new Address(160, to)),
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
    public static ExecutorGuardedIntent load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new ExecutorGuardedIntent(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static ExecutorGuardedIntent load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new ExecutorGuardedIntent(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static ExecutorGuardedIntent load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new ExecutorGuardedIntent(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static ExecutorGuardedIntent load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new ExecutorGuardedIntent(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<ExecutorGuardedIntent> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(ExecutorGuardedIntent.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<ExecutorGuardedIntent> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(ExecutorGuardedIntent.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<ExecutorGuardedIntent> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(ExecutorGuardedIntent.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<ExecutorGuardedIntent> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(ExecutorGuardedIntent.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
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
            super(new Bytes32(id),
                    new Bytes32(gameType),
                    new Bytes32(gameSessionId),
                    new Uint256(score),
                    new Uint256(startTime),
                    new DynamicBytes(startMessage),
                    new DynamicBytes(startSign),
                    new Uint256(completeTime),
                    new DynamicBytes(completeMessage),
                    new DynamicBytes(completeSign),
                    new Uint256(loadScore));
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
