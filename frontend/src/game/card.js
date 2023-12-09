import exterminate from '../static/images/Exterminate.jpg';
import explore from '../static/images/Explore.jpg';
import expand from '../static/images/Expand.jpg';

const MediaCard = ({ title, imageUrl, onUse }) => {
  const cardStyles = {
    maxWidth: '60%',
    border: '1px solid #ccc',
    borderRadius: '4px',
    margin: '10px',
    padding: '10px',
  };

  const mediaStyles = {
    height: '500px',
    backgroundImage: `url("${imageUrl}")`,
    backgroundSize: 'cover',
    backgroundPosition: 'center',
  };

  const buttonStyles = {
    padding: '8px 16px',
    margin: '4px',
    fontSize: '14px',
    cursor: 'pointer',
  };

  return (
    <div style={cardStyles}>
      <div style={mediaStyles} title={title} />
      <div>
        <h2 style={{ marginBottom: '8px' }}>{title}</h2>
      </div>
      <div>
        <button style={buttonStyles} onClick={onUse}>
          Usar
        </button>
      </div>
    </div>
  );
};

const Cards = () => {
  const handleExpand = () => {
    console.log('Usar expansión');
  };

  const handleExplore = () => {
    console.log('Usar explorar');
  };

  const handleExterminate = () => {
    console.log('Usar exterminar');
  };

  const cardsContainerStyle = {
    display: 'flex',
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
    flexWrap: 'wrap',
    marginTop: '20px',
  };

  return (
    <div>
        <div style={cardsContainerStyle}>
        <MediaCard
        title="Expand"
        imageUrl={expand}
        onUse={handleExpand}
      />
      <MediaCard
        title="Explore"
        imageUrl={explore}
        onUse={handleExplore}
      />
      <MediaCard
        title="Exterminate"
        imageUrl={exterminate}
        onUse={handleExterminate}
      />
        </div>
    </div>
  );
};

export default Cards;
