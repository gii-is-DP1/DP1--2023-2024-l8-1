import "../SearchResult.css";

export const GameResult = ({ result, onSelectGame }) => {
    return (
        <div
            className="search-result"
            onClick={() => onSelectGame(result)}
        >
            {result.name}
        </div>
    );
};
