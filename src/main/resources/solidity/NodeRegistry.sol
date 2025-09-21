// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

contract NodeRegistry {
    struct NodeInfo {
        string url;
        bool exists;
    }

    mapping(address => NodeInfo) public nodes;
    address[] public registeredNodes; // 已注册节点地址列表

    event NodeRegistered(address indexed node, string url);
    event NodeUrlUpdated(address indexed node, string newUrl);

    // 节点注册
    function registerNode(string calldata url) external {
        require(bytes(url).length > 0, "URL required");

        // 如果是新节点，添加到已注册列表
        if (!nodes[msg.sender].exists) {
            registeredNodes.push(msg.sender);
        }

        nodes[msg.sender] = NodeInfo(url, true);
        emit NodeRegistered(msg.sender, url);
    }

    // 更新 URL
    function updateNodeUrl(string calldata newUrl) external {
        require(nodes[msg.sender].exists, "Node not registered");
        require(bytes(newUrl).length > 0, "URL required");
        nodes[msg.sender].url = newUrl;
        emit NodeUrlUpdated(msg.sender, newUrl);
    }

    // 查询节点信息
    function getNode(address nodeAddress) external view returns (string memory url, bool exists) {
        NodeInfo storage node = nodes[nodeAddress];
        return (node.url, node.exists);
    }

    // 检查节点是否注册
    function isRegistered(address nodeAddress) external view returns (bool) {
        return nodes[nodeAddress].exists;
    }

    // 返回已注册节点总数
    function getRegisteredNodeCount() external view returns (uint256) {
        return registeredNodes.length;
    }

    function getNodesByPage(uint256 offset, uint256 limit) external view returns (address[] memory addrs, string[] memory urls) {
        uint256 total = registeredNodes.length;

        if (offset >= total) {
            return (new address[](0) , new string[](0) );
        }

        uint256 end = offset + limit;
        if (end > total) {
            end = total;
        }

        uint256 size = end - offset;
        address[] memory _addrs = new address[](size);
        string[] memory _urls = new string[](size);

        for (uint256 i = 0; i < size; i++) {
            address nodeAddr = registeredNodes[offset + i];
            _addrs[i] = nodeAddr;
            _urls[i] = nodes[nodeAddr].url;
        }

        return (_addrs, _urls);
    }
}
