import {useState} from 'react';

// Sample player data
const initialPlayers = [
  {
    id: 1,
    name: 'Rohit Sharma',
    role: 'Batter',
    batting: 95,
    bowling: 45,
    fielding: 80
  },
  {
    id: 2,
    name: 'Virat Kohli',
    role: 'Batter',
    batting: 98,
    bowling: 40,
    fielding: 85
  },
  {
    id: 3,
    name: 'KL Rahul',
    role: 'Wicket Keeper',
    batting: 88,
    bowling: 35,
    fielding: 82
  },
  {
    id: 4,
    name: 'Jasprit Bumrah',
    role: 'Bowler',
    batting: 35,
    bowling: 98,
    fielding: 75
  },
  {
    id: 5,
    name: 'Ravindra Jadeja',
    role: 'All-Rounder',
    batting: 75,
    bowling: 82,
    fielding: 95
  },
  {
    id: 6,
    name: 'Hardik Pandya',
    role: 'All-Rounder',
    batting: 78,
    bowling: 75,
    fielding: 88
  },
  {
    id: 7,
    name: 'Mohammed Shami',
    role: 'Bowler',
    batting: 30,
    bowling: 92,
    fielding: 70
  },
  {
    id: 8,
    name: 'Shubman Gill',
    role: 'Batter',
    batting: 85,
    bowling: 25,
    fielding: 78
  },
  {
    id: 9,
    name: 'Rishabh Pant',
    role: 'Wicket Keeper',
    batting: 82,
    bowling: 20,
    fielding: 75
  },
  {
    id: 10,
    name: 'Yuzvendra Chahal',
    role: 'Bowler',
    batting: 28,
    bowling: 88,
    fielding: 68
  },
];

export default function TeamSelection() {
  const [availablePlayers, setAvailablePlayers] = useState(initialPlayers);
  const [selectedPlayers, setSelectedPlayers] = useState([]);
  const [selectedCard, setSelectedCard] = useState(null);
  const [showWelcome, setShowWelcome] = useState(true);
  const [sortBy, setSortBy] = useState('name');

  // Custom event handlers matching the specification
  const welcome = () => setShowWelcome(true);
  const setWelcome = (value) => setShowWelcome(value);

  const getRoleCount = (role) => {
    return selectedPlayers.filter(p => p.role === role).length;
  };

  const isValidSelection = () => {
    const batters = getRoleCount('Batter');
    const bowlers = getRoleCount('Bowler');
    const keepers = getRoleCount('Wicket Keeper');
    const allRounders = getRoleCount('All-Rounder');

    return (
        selectedPlayers.length <= 11 &&
        batters >= 3 && batters <= 6 &&
        bowlers >= 3 && bowlers <= 6 &&
        keepers === 1 &&
        allRounders >= 1 && allRounders <= 4
    );
  };

  const addPlayer = (index) => {
    const player = sortedAvailablePlayers[index];
    if (selectedPlayers.length < 11 && player) {
      setSelectedPlayers([...selectedPlayers, player]);
      setAvailablePlayers(availablePlayers.filter(p => p.id !== player.id));
    }
  };

  const removePlayer = (index) => {
    const player = selectedPlayers[index];
    if (player) {
      setSelectedPlayers(selectedPlayers.filter(p => p.id !== player.id));
      setAvailablePlayers([...availablePlayers, player]);
    }
  };

  const showPlayerDetailCard = (i) => {
    const player = sortedAvailablePlayers[i];
    if (player) {
      setSelectedCard(player);
    }
  };

  const closeCard = () => {
    setSelectedCard(null);
  };

  const sortedAvailablePlayers = [...availablePlayers].sort((a, b) => {
    if (sortBy === 'name') {
      return a.name.localeCompare(b.name);
    }
    if (sortBy === 'role') {
      return a.role.localeCompare(b.role);
    }
    if (sortBy === 'batting') {
      return b.batting - a.batting;
    }
    if (sortBy === 'bowling') {
      return b.bowling - a.bowling;
    }
    return 0;
  });

  return (
      <div className="min-h-screen bg-gray-900 text-white p-6">
        <div className="max-w-7xl mx-auto">
          <h1 className="text-4xl font-bold mb-4">Cricket Team Selection</h1>
          <p className="text-gray-300 mb-6">
            Create a React Team Selection component that displays a list of
            available players,
            allows users to view their details, select or remove them from a
            team, and sort the list by player attributes.
          </p>

          <button
              onClick={() => setShowWelcome(!showWelcome)}
              className="mb-6 px-4 py-2 bg-blue-600 hover:bg-blue-700 rounded"
          >
            {showWelcome ? 'Hide' : 'Show'} Instructions
          </button>

          {showWelcome && (
              <div className="bg-gray-800 p-6 rounded-lg mb-6">
                <h2 className="text-2xl font-bold mb-4">Welcome to Team
                  Selection!</h2>
                <div className="space-y-2 text-gray-300">
                  <p>• See all available players displayed in a list, showing
                    each player's:</p>
                  <ul className="ml-8 space-y-1">
                    <li>○ Name</li>
                    <li>○ Role (Batter, Bowler, All-Rounder, Wicket Keeper)</li>
                    <li>○ Batting Skill</li>
                    <li>○ Bowling Skill</li>
                    <li>○ Fielding Skill</li>
                  </ul>
                  <p>• Click on a player's name to open a detailed card showing
                    all attributes.</p>
                  <p>• Close a player's detail card using the (x) button.</p>
                  <p>• Click the Select button next to a player to:</p>
                  <ul className="ml-8 space-y-1">
                    <li>○ Add them to the Selected Players list.</li>
                    <li>○ Disable their Select button in the available players
                      list.
                    </li>
                  </ul>
                  <p>• View and manage the Selected Players list:</p>
                  <ul className="ml-8 space-y-1">
                    <li>○ See all chosen players.</li>
                    <li>○ Click Remove to take a player out of the team.</li>
                    <li>○ Once removed, their Select button becomes active again
                      in the available list.
                    </li>
                  </ul>
                  <p>• Sort the available players by Name, Role, Batting Skill,
                    or Bowling Skill.</p>
                  <p>• Follow team composition rules while selecting
                    players:</p>
                  <ul className="ml-8 space-y-1">
                    <li>○ Maximum 11 players in total.</li>
                    <li>○ 3–6 Batters allowed.</li>
                    <li>○ 3–6 Bowlers allowed.</li>
                    <li>○ 1 Wicket Keeper required.</li>
                    <li>○ 1–4 All-Rounders allowed.</li>
                  </ul>
                  <p className="mt-4 text-yellow-400">
                    Current Status: {isValidSelection()
                      ? '✓ Valid team composition'
                      : '✗ Invalid team composition'}
                  </p>
                </div>
                <button
                    onClick={() => setShowWelcome(false)}
                    className="mt-4 px-4 py-2 bg-gray-700 hover:bg-gray-600 rounded"
                >
                  Close (×)
                </button>
              </div>
          )}

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Available Players */}
            <div className="bg-gray-800 p-6 rounded-lg">
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-2xl font-bold">Available Players</h2>
                <select
                    value={sortBy}
                    onChange={(e) => setSortBy(e.target.value)}
                    className="bg-gray-700 px-3 py-2 rounded"
                    data-testid="sort-select"
                >
                  <option value="name">Sort by Name</option>
                  <option value="role">Sort by Role</option>
                  <option value="batting">Sort by Batting</option>
                  <option value="bowling">Sort by Bowling</option>
                </select>
              </div>

              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead>
                  <tr className="border-b border-gray-700">
                    <th className="text-left p-2">Name</th>
                    <th className="text-left p-2">Role</th>
                    <th className="text-left p-2">Actions</th>
                  </tr>
                  </thead>
                  <tbody data-testid="available-players-table">
                  {sortedAvailablePlayers.map((player, index) => (
                      <tr
                          key={player.id}
                          className="border-b border-gray-700 hover:bg-gray-700"
                          data-testid={`available-${player.name.replace(/\s+/g,
                              '-')}-row`}
                      >
                        <td className="p-2">
                          <button
                              onClick={() => showPlayerDetailCard(index)}
                              className="text-blue-400 hover:text-blue-300"
                              data-testid={`available-${player.name.replace(
                                  /\s+/g, '-')}-name`}
                          >
                            {player.name}
                          </button>
                        </td>
                        <td className="p-2"
                            data-testid={`available-${player.name.replace(
                                /\s+/g, '-')}-role`}>
                          {player.role}
                        </td>
                        <td className="p-2">
                          <button
                              onClick={() => addPlayer(index)}
                              disabled={selectedPlayers.length >= 11}
                              className="px-4 py-1 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-600 disabled:cursor-not-allowed rounded"
                              data-testid={`available-${player.name.replace(
                                  /\s+/g, '-')}-select`}
                          >
                            Select
                          </button>
                        </td>
                      </tr>
                  ))}
                  </tbody>
                </table>
              </div>
            </div>

            {/* Selected Players */}
            <div className="bg-gray-800 p-6 rounded-lg">
              <h2 className="text-2xl font-bold mb-4">
                Selected Players ({selectedPlayers.length}/11)
              </h2>

              <div className="mb-4 text-sm text-gray-300 space-y-1">
                <p>Batters: {getRoleCount('Batter')} (3-6 required)</p>
                <p>Bowlers: {getRoleCount('Bowler')} (3-6 required)</p>
                <p>Wicket Keeper: {getRoleCount('Wicket Keeper')} (1
                  required)</p>
                <p>All-Rounders: {getRoleCount('All-Rounder')} (1-4
                  required)</p>
              </div>

              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead>
                  <tr className="border-b border-gray-700">
                    <th className="text-left p-2">Name</th>
                    <th className="text-left p-2">Role</th>
                    <th className="text-left p-2">Actions</th>
                  </tr>
                  </thead>
                  <tbody data-testid="selected-players-table">
                  {selectedPlayers.map((player, index) => (
                      <tr
                          key={player.id}
                          className="border-b border-gray-700 hover:bg-gray-700"
                          data-testid={`selected-${player.name.replace(/\s+/g,
                              '-')}-row`}
                      >
                        <td className="p-2">{player.name}</td>
                        <td className="p-2">{player.role}</td>
                        <td className="p-2">
                          <button
                              onClick={() => removePlayer(index)}
                              className="px-4 py-1 bg-red-600 hover:bg-red-700 rounded"
                              data-testid={`selected-${player.name.replace(
                                  /\s+/g, '-')}-remove`}
                          >
                            Remove
                          </button>
                        </td>
                      </tr>
                  ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          {/* Player Detail Card */}
          {selectedCard && (
              <div
                  className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4">
                <div
                    className="bg-gray-800 rounded-lg p-6 max-w-md w-full"
                    style={{
                      flex: '1',
                      overflow: 'auto',
                      maxHeight: '90vh',
                      marginRight: '10px'
                    }}
                    data-testid={`player-${selectedCard.name.replace(/\s+/g,
                        '-')}-details`}
                >
                  <div className="flex justify-between items-start mb-4">
                    <h1 className="text-2xl font-bold">Player Detail</h1>
                    <button
                        onClick={closeCard}
                        className="text-gray-400 hover:text-white text-2xl"
                        data-testid={`player-detail-${selectedCard.name.replace(
                            /\s+/g, '-')}-close`}
                    >
                      ×
                    </button>
                  </div>

                  <div className="space-y-3">
                    <p>
                      <strong>Name:</strong>{' '}
                      <span
                          data-testid={`player-detail-${selectedCard.name.replace(
                              /\s+/g, '-')}-name`}>
                    {selectedCard.name}
                  </span>
                    </p>
                    <p>
                      <strong>Role:</strong>{' '}
                      <span
                          data-testid={`player-detail-${selectedCard.name.replace(
                              /\s+/g, '-')}-role`}>
                    {selectedCard.role}
                  </span>
                    </p>
                    <p>
                      <strong>Batting:</strong>{' '}
                      <span
                          data-testid={`player-detail-${selectedCard.name.replace(
                              /\s+/g, '-')}-batting`}>
                    {selectedCard.batting}
                  </span>
                    </p>
                    <p>
                      <strong>Bowling:</strong>{' '}
                      <span
                          data-testid={`player-detail-${selectedCard.name.replace(
                              /\s+/g, '-')}-bowling`}>
                    {selectedCard.bowling}
                  </span>
                    </p>
                    <p>
                      <strong>Fielding:</strong>{' '}
                      <span
                          data-testid={`player-detail-${selectedCard.name.replace(
                              /\s+/g, '-')}-fielding`}>
                    {selectedCard.fielding}
                  </span>
                    </p>
                  </div>

                  <button
                      onClick={() => {
                        const playerIndex = availablePlayers.findIndex(
                            p => p.id === selectedCard.id);
                        if (playerIndex >= 0) {
                          addPlayer(sortedAvailablePlayers.findIndex(
                              p => p.id === selectedCard.id));
                        }
                        closeCard();
                      }}
                      disabled={!availablePlayers.find(
                              p => p.id === selectedCard.id)
                          || selectedPlayers.length >= 11}
                      className="mt-6 w-full px-4 py-2 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-600 disabled:cursor-not-allowed rounded"
                      data-testid={`player-detail-${selectedCard.name.replace(
                          /\s+/g, '-')}-add`}
                  >
                    {availablePlayers.find(p => p.id === selectedCard.id)
                        ? 'Select' : 'Already Selected'}
                  </button>
                </div>
              </div>
          )}
        </div>
      </div>
  );
}