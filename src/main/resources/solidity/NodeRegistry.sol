// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

contract NodeRegistry {
    struct NodeInfo {
        string url;
        bool exists;
    }

    mapping(address => NodeInfo) public nodes;

    event NodeRegistered(address indexed node, string url);
    event NodeUrlUpdated(address indexed node, string newUrl);

    // 节点注册
    function registerNode(string calldata url) external {
        require(bytes(url).length > 0, "URL required");
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
}
