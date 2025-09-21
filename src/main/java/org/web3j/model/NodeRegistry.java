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
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
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
public class NodeRegistry extends Contract {
    public static final String BINARY = "6080604052348015600e575f5ffd5b50610c258061001c5f395ff3fe608060405234801561000f575f5ffd5b5060043610610085575f3560e01c806374ea9be51161005857806374ea9be5146101045780639d20904814610125578063be6522d614610138578063c3c5a5471461014b575f5ffd5b8063189a5a171461008957806327c6f43e146100b357806329173a92146100c857806361ca89fa146100d9575b5f5ffd5b61009c610097366004610794565b610189565b6040516100aa9291906107ef565b60405180910390f35b6100c66100c1366004610812565b61022d565b005b6001546040519081526020016100aa565b6100ec6100e7366004610880565b610381565b6040516001600160a01b0390911681526020016100aa565b610117610112366004610897565b6103a9565b6040516100aa9291906108b7565b61009c610133366004610794565b6105ee565b6100c6610146366004610812565b6106ac565b610179610159366004610794565b6001600160a01b03165f9081526020819052604090206001015460ff1690565b60405190151581526020016100aa565b5f602081905290815260409020805481906101a390610960565b80601f01602080910402602001604051908101604052809291908181526020018280546101cf90610960565b801561021a5780601f106101f15761010080835404028352916020019161021a565b820191905f5260205f20905b8154815290600101906020018083116101fd57829003601f168201915b5050506001909301549192505060ff1682565b8061026e5760405162461bcd60e51b815260206004820152600c60248201526b155493081c995c5d5a5c995960a21b60448201526064015b60405180910390fd5b335f9081526020819052604090206001015460ff166102c8576001805480820182555f919091527fb10e2d527612073b26eecdfd717e6a320cf44b4afac2b0732d9fcbe2b7fa0cf60180546001600160a01b031916331790555b6040805160606020601f850181900402820181018352918101838152909182919085908590819085018382808284375f920182905250938552505060016020938401525033815290819052604090208151819061032590826109f8565b50602091909101516001909101805460ff191691151591909117905560405133907f27dcab8cdfa14145566e2b9d04fea5a4e8ea320a3f04488a1de4d431ed1080ab906103759085908590610ab3565b60405180910390a25050565b60018181548110610390575f80fd5b5f918252602090912001546001600160a01b0316905081565b60015460609081908085106103f557604080515f808252602082018181528284019093529091906103ea565b60608152602001906001900390816103d55790505b5092509250506105e7565b5f6104008587610af5565b90508181111561040d5750805b5f6104188783610b0e565b90505f8167ffffffffffffffff81111561043457610434610998565b60405190808252806020026020018201604052801561045d578160200160208202803683370190505b5090505f8267ffffffffffffffff81111561047a5761047a610998565b6040519080825280602002602001820160405280156104ad57816020015b60608152602001906001900390816104985790505b5090505f5b838110156105dd575f60016104c7838d610af5565b815481106104d7576104d7610b21565b905f5260205f20015f9054906101000a90046001600160a01b031690508084838151811061050757610507610b21565b6001600160a01b039283166020918202929092018101919091529082165f9081529081905260409020805461053b90610960565b80601f016020809104026020016040519081016040528092919081815260200182805461056790610960565b80156105b25780601f10610589576101008083540402835291602001916105b2565b820191905f5260205f20905b81548152906001019060200180831161059557829003601f168201915b50505050508383815181106105c9576105c9610b21565b6020908102919091010152506001016104b2565b5090955093505050505b9250929050565b6001600160a01b0381165f908152602081905260408120600181015481546060939291829160ff90911690829061062490610960565b80601f016020809104026020016040519081016040528092919081815260200182805461065090610960565b801561069b5780601f106106725761010080835404028352916020019161069b565b820191905f5260205f20905b81548152906001019060200180831161067e57829003601f168201915b505050505091509250925050915091565b335f9081526020819052604090206001015460ff166107035760405162461bcd60e51b8152602060048201526013602482015272139bd919481b9bdd081c9959da5cdd195c9959606a1b6044820152606401610265565b8061073f5760405162461bcd60e51b815260206004820152600c60248201526b155493081c995c5d5a5c995960a21b6044820152606401610265565b335f908152602081905260409020610758828483610b35565b50336001600160a01b03167f4505168a8705a16fd4d0575197fd0f510db69df93a065e158ad2c0957ba12bac8383604051610375929190610ab3565b5f602082840312156107a4575f5ffd5b81356001600160a01b03811681146107ba575f5ffd5b9392505050565b5f81518084528060208401602086015e5f602082860101526020601f19601f83011685010191505092915050565b604081525f61080160408301856107c1565b905082151560208301529392505050565b5f5f60208385031215610823575f5ffd5b823567ffffffffffffffff811115610839575f5ffd5b8301601f81018513610849575f5ffd5b803567ffffffffffffffff81111561085f575f5ffd5b856020828401011115610870575f5ffd5b6020919091019590945092505050565b5f60208284031215610890575f5ffd5b5035919050565b5f5f604083850312156108a8575f5ffd5b50508035926020909101359150565b604080825283519082018190525f9060208501906060840190835b818110156108f95783516001600160a01b03168352602093840193909201916001016108d2565b50508381036020850152809150845180825260208201925060208160051b830101602087015f5b8381101561095257601f1985840301865261093c8383516107c1565b6020968701969093509190910190600101610920565b509098975050505050505050565b600181811c9082168061097457607f821691505b60208210810361099257634e487b7160e01b5f52602260045260245ffd5b50919050565b634e487b7160e01b5f52604160045260245ffd5b601f8211156109f357805f5260205f20601f840160051c810160208510156109d15750805b601f840160051c820191505b818110156109f0575f81556001016109dd565b50505b505050565b815167ffffffffffffffff811115610a1257610a12610998565b610a2681610a208454610960565b846109ac565b6020601f821160018114610a58575f8315610a415750848201515b5f19600385901b1c1916600184901b1784556109f0565b5f84815260208120601f198516915b82811015610a875787850151825560209485019460019092019101610a67565b5084821015610aa457868401515f19600387901b60f8161c191681555b50505050600190811b01905550565b60208152816020820152818360408301375f818301604090810191909152601f909201601f19160101919050565b634e487b7160e01b5f52601160045260245ffd5b80820180821115610b0857610b08610ae1565b92915050565b81810381811115610b0857610b08610ae1565b634e487b7160e01b5f52603260045260245ffd5b67ffffffffffffffff831115610b4d57610b4d610998565b610b6183610b5b8354610960565b836109ac565b5f601f841160018114610b92575f8515610b7b5750838201355b5f19600387901b1c1916600186901b1783556109f0565b5f83815260208120601f198716915b82811015610bc15786850135825560209485019460019092019101610ba1565b5086821015610bdd575f1960f88860031b161c19848701351681555b505060018560011b018355505050505056fea2646970667358221220b08838c928bd532e1ac6690269cba9e05e9623f107266dc27701a947adb13c5d64736f6c634300081d0033";

    public static final String FUNC_GETNODE = "getNode";

    public static final String FUNC_GETNODESBYPAGE = "getNodesByPage";

    public static final String FUNC_GETREGISTEREDNODECOUNT = "getRegisteredNodeCount";

    public static final String FUNC_ISREGISTERED = "isRegistered";

    public static final String FUNC_NODES = "nodes";

    public static final String FUNC_REGISTERNODE = "registerNode";

    public static final String FUNC_REGISTEREDNODES = "registeredNodes";

    public static final String FUNC_UPDATENODEURL = "updateNodeUrl";

    public static final Event NODEREGISTERED_EVENT = new Event("NodeRegistered", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event NODEURLUPDATED_EVENT = new Event("NodeUrlUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Utf8String>() {}));
    ;

    @Deprecated
    protected NodeRegistry(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected NodeRegistry(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected NodeRegistry(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected NodeRegistry(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<NodeRegisteredEventResponse> getNodeRegisteredEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(NODEREGISTERED_EVENT, transactionReceipt);
        ArrayList<NodeRegisteredEventResponse> responses = new ArrayList<NodeRegisteredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            NodeRegisteredEventResponse typedResponse = new NodeRegisteredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.node = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.url = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static NodeRegisteredEventResponse getNodeRegisteredEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(NODEREGISTERED_EVENT, log);
        NodeRegisteredEventResponse typedResponse = new NodeRegisteredEventResponse();
        typedResponse.log = log;
        typedResponse.node = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.url = (String) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<NodeRegisteredEventResponse> nodeRegisteredEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getNodeRegisteredEventFromLog(log));
    }

    public Flowable<NodeRegisteredEventResponse> nodeRegisteredEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NODEREGISTERED_EVENT));
        return nodeRegisteredEventFlowable(filter);
    }

    public static List<NodeUrlUpdatedEventResponse> getNodeUrlUpdatedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(NODEURLUPDATED_EVENT, transactionReceipt);
        ArrayList<NodeUrlUpdatedEventResponse> responses = new ArrayList<NodeUrlUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            NodeUrlUpdatedEventResponse typedResponse = new NodeUrlUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.node = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newUrl = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static NodeUrlUpdatedEventResponse getNodeUrlUpdatedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(NODEURLUPDATED_EVENT, log);
        NodeUrlUpdatedEventResponse typedResponse = new NodeUrlUpdatedEventResponse();
        typedResponse.log = log;
        typedResponse.node = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.newUrl = (String) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<NodeUrlUpdatedEventResponse> nodeUrlUpdatedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getNodeUrlUpdatedEventFromLog(log));
    }

    public Flowable<NodeUrlUpdatedEventResponse> nodeUrlUpdatedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NODEURLUPDATED_EVENT));
        return nodeUrlUpdatedEventFlowable(filter);
    }

    public RemoteFunctionCall<Tuple2<String, Boolean>> getNode(String nodeAddress) {
        final Function function = new Function(FUNC_GETNODE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, nodeAddress)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Bool>() {}));
        return new RemoteFunctionCall<Tuple2<String, Boolean>>(function,
                new Callable<Tuple2<String, Boolean>>() {
                    @Override
                    public Tuple2<String, Boolean> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<String, Boolean>(
                                (String) results.get(0).getValue(), 
                                (Boolean) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<Tuple2<List<String>, List<String>>> getNodesByPage(BigInteger offset, BigInteger limit) {
        final Function function = new Function(FUNC_GETNODESBYPAGE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(offset), 
                new org.web3j.abi.datatypes.generated.Uint256(limit)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}));
        return new RemoteFunctionCall<Tuple2<List<String>, List<String>>>(function,
                new Callable<Tuple2<List<String>, List<String>>>() {
                    @Override
                    public Tuple2<List<String>, List<String>> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<List<String>, List<String>>(
                                convertToNative((List<Address>) results.get(0).getValue()), 
                                convertToNative((List<Utf8String>) results.get(1).getValue()));
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> getRegisteredNodeCount() {
        final Function function = new Function(FUNC_GETREGISTEREDNODECOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Boolean> isRegistered(String nodeAddress) {
        final Function function = new Function(FUNC_ISREGISTERED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, nodeAddress)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<Tuple2<String, Boolean>> nodes(String param0) {
        final Function function = new Function(FUNC_NODES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Bool>() {}));
        return new RemoteFunctionCall<Tuple2<String, Boolean>>(function,
                new Callable<Tuple2<String, Boolean>>() {
                    @Override
                    public Tuple2<String, Boolean> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<String, Boolean>(
                                (String) results.get(0).getValue(), 
                                (Boolean) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> registerNode(String url) {
        final Function function = new Function(
                FUNC_REGISTERNODE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(url)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> registeredNodes(BigInteger param0) {
        final Function function = new Function(FUNC_REGISTEREDNODES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> updateNodeUrl(String newUrl) {
        final Function function = new Function(
                FUNC_UPDATENODEURL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(newUrl)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static NodeRegistry load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new NodeRegistry(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static NodeRegistry load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new NodeRegistry(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static NodeRegistry load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new NodeRegistry(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static NodeRegistry load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new NodeRegistry(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<NodeRegistry> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(NodeRegistry.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<NodeRegistry> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(NodeRegistry.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<NodeRegistry> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(NodeRegistry.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<NodeRegistry> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(NodeRegistry.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class NodeRegisteredEventResponse extends BaseEventResponse {
        public String node;

        public String url;
    }

    public static class NodeUrlUpdatedEventResponse extends BaseEventResponse {
        public String node;

        public String newUrl;
    }
}
