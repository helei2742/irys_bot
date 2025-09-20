// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

contract ExecutorGuardedIntentWithScore {
    address public owner;
    uint256 public constant MAX_LOAD = 80;

    struct IntentData {
        bytes32 id;
        bytes32 gameType;
        bytes32 gameSessionId;
        uint256 score;
        uint256 startTime;
        bytes startMessage;
        bytes startSign;
        uint256 completeTime;
        bytes completeMessage;
        bytes completeSign;
        uint256 loadScore;
    }

    event IntentRegistered(address indexed user, bytes32 indexed intentId);
    event IntentClaimed(bytes32 indexed intentId, address indexed executor);
    event IntentExecuted(bytes32 indexed intentId, address indexed executor);
    event RewardWithdrawn(address indexed executor, uint256 amount);

    // 存储
    mapping(bytes32 => IntentData) private intents;       // intentId => IntentData
    mapping(bytes32 => address) public executor;         // intentId => 执行者
    mapping(bytes32 => bool) private usedMessages;       // 防止 start/complete 消息重复
    mapping(bytes32 => bool) private executed;           // 防止重复执行
    mapping(address => uint256) public executionCount;   // 节点执行次数

    constructor() {
        owner = msg.sender;
    }

    // 注册 Intent
    function registerIntent(IntentData calldata data) external {
        require(intents[data.id].startTime == 0, "Intent already exists");

        require(data.id != bytes32(0), "id required");
        require(data.gameType != bytes32(0), "gameType required");
        require(data.gameSessionId != bytes32(0), "gameSessionId required");
        require(data.score > 0, "score must be positive");
        require(data.startTime > 0, "startTime required");
        require(data.startMessage.length > 0, "startMessage required");
        require(data.startSign.length > 0, "startSign required");
        require(data.completeTime > 0, "completeTime required");
        require(data.completeMessage.length > 0, "completeMessage required");
        require(data.completeSign.length > 0, "completeSign required");
        require(data.loadScore > 0, "loadScore must be positive");
        require(data.loadScore <= MAX_LOAD, "Executor overloaded");
        require(data.completeTime > data.startTime, "Complete time must be after start time");

        bytes32 startHash = keccak256(data.startMessage);
        bytes32 completeHash = keccak256(data.completeMessage);
        require(!usedMessages[startHash], "startMessage already used");
        require(!usedMessages[completeHash], "completeMessage already used");

        usedMessages[startHash] = true;
        usedMessages[completeHash] = true;

        intents[data.id] = data;

        emit IntentRegistered(msg.sender, data.id);
    }

    // Claim Intent（非 view）
    function claimIntent(bytes32 intentId) external {
        require(intents[intentId].startTime != 0, "Intent does not exist");
        require(executor[intentId] == address(0), "Intent already claimed");

        executor[intentId] = msg.sender;

        emit IntentClaimed(intentId, msg.sender);
    }

    // 获取 IntentData（view，不修改状态）
    function getIntent(bytes32 intentId) external view returns (IntentData memory) {
        return intents[intentId];
    }

    // 执行 Intent
    function executeIntent(bytes32 intentId) external {
        require(executor[intentId] == msg.sender, "Not authorized");
        require(!executed[intentId], "Intent already executed");

        executed[intentId] = true;
        executionCount[msg.sender] += 1;

        emit IntentExecuted(intentId, msg.sender);
    }

    // 提取奖励
    function withdrawReward() external {
        uint256 count = executionCount[msg.sender];
        require(count > 0, "No reward to withdraw");

        uint256 contractBalance = address(this).balance;
        uint256 reward = contractBalance >= count ? count : contractBalance;

        executionCount[msg.sender] = 0;
        payable(msg.sender).transfer(reward);

        emit RewardWithdrawn(msg.sender, reward);
    }

    receive() external payable {}

    function withdraw(address payable to) external {
        require(msg.sender == owner, "Only owner");
        to.transfer(address(this).balance);
    }
}
