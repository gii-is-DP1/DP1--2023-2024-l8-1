import "../SearchResult.css";

export const PlayerResult = ({ result, onSelectPlayer }) => {
    return (
        <div
            className="search-result"
            onClick={() => onSelectPlayer(result)}
        >
            {result.user.username}
        </div>
    );
};
