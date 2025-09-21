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

    struct UserIntent {
        bytes32 id;
        uint256 submitTime;
        address executor;
        IntentStatus status;
    }

    enum IntentStatus {
        ON_CHAIN,
        WAITING,
        RUNNING,
        COMPLETED,
        CANCELLED,
        ERROR,
        UNKNOWN
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
    mapping(address => mapping(bytes32 => UserIntent)) private userIntentMap;  // 用户 => 提交的 intentId + 时间
    mapping(address => bytes32[]) private userIntentIds;

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

        userIntentMap[msg.sender][data.id] = UserIntent({
            id: data.id,
            submitTime: block.timestamp,
            executor: address(0),
            status: IntentStatus.ON_CHAIN
        });

        userIntentIds[msg.sender].push(data.id);

        emit IntentRegistered(msg.sender, data.id);
    }

    // 分页获取用户的 intents
    function getUserIntentsPaged(
        address user,
        uint256 offset,
        uint256 limit
    ) external view returns (UserIntent[] memory page, uint256 total) {
        bytes32[] storage ids = userIntentIds[user];
        total = ids.length;

        if (offset >= total) {
            return (new UserIntent[](0), total);
        }

        uint256 end = offset + limit;
        if (end > total) {
            end = total;
        }

        uint256 size = end - offset;
        page = new UserIntent[](size);

        for (uint256 i = 0; i < size; i++) {
            page[i] = userIntentMap[user][ids[offset + i]];
        }

        return (page, total);
    }

    // 查询用户 intent 总数
    function getUserIntentCount(address user) external view returns (uint256) {
        return userIntentIds[user].length;
    }

    // Claim Intent（非 view）
    function claimIntent(bytes32 intentId, address target) external {
        require(intents[intentId].startTime != 0, "Intent does not exist");
        require(executor[intentId] == address(0), "Intent already claimed");

        executor[intentId] = msg.sender;
        userIntentMap[target][intentId].executor = msg.sender;
        userIntentMap[target][intentId].status = IntentStatus.WAITING;

        emit IntentClaimed(intentId, msg.sender);
    }

    // 获取 IntentData（view，不修改状态）
    function getIntent(bytes32 intentId) external view returns (IntentData memory) {
        return intents[intentId];
    }

    // 节点提交错误
    function commitError(bytes32 intentId, address target) external {
        require(executor[intentId] == msg.sender, "Not authorized");
        require(userIntentMap[target][intentId].status != IntentStatus.ERROR, "already error");
        require(!executed[intentId], "Intent already executed");
        userIntentMap[target][intentId].status = IntentStatus.ERROR;
        executed[intentId] = true;
    }

    // 节点开始执行
    function startExecuteIntent(bytes32 intentId, address target) external {
        require(executor[intentId] == msg.sender, "Not authorized");
        require(userIntentMap[target][intentId].status == IntentStatus.WAITING, "Status error");

        uint256 nowMs = block.timestamp * 1000;
        if (nowMs < intents[intentId].startTime) {
            revert("Start time not reach");
        }
        if (nowMs >= intents[intentId].startTime + 60_000) {
            userIntentMap[target][intentId].status = IntentStatus.CANCELLED;
            revert("Start time out of recently 60s");
        } else {
            userIntentMap[target][intentId].status = IntentStatus.RUNNING;
        }
    }

    // 节点完成执行
    function completeExecuteIntent(bytes32 intentId, address target) external {
        require(!executed[intentId], "Intent already executed");
        require(executor[intentId] == msg.sender, "Not authorized");
        require(userIntentMap[target][intentId].status == IntentStatus.RUNNING, "Status error");

        uint256 nowMs = block.timestamp * 1000;
        if (nowMs < intents[intentId].completeTime) {
            require(true, "complete time not reached");
        }

        if ( nowMs >= intents[intentId].completeTime + 60_000) {
            userIntentMap[target][intentId].status = IntentStatus.CANCELLED;
            require(true, "complete time out of recently 60s");
        } else {
            userIntentMap[target][intentId].status = IntentStatus.COMPLETED;
        }

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
