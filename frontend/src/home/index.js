import React from 'react';
import '../App.css';
import '../static/css/home/home.css';

export default function Home() {
    return (
        <div className="scrollable-content">
            <div className="content-container">
                <div className="column-left">
                    <img
                        src="/PocketImperium.jpg"
                        alt="Pocket Imperium"
                        className="background-image"
                    />
                </div>
                <div className="column-right">
                    <div className="features">
                        <h1>Características del Juego</h1>
                        <ul>
                            <li>Modo de juego único para 3 jugadores.</li>
                            <li>Expansión, exploración y exterminio estratégicos.</li>
                            <li>Puntuación basada en sistemas conquistados.</li>
                        </ul>
                    </div>
                </div>
            </div>
            <div className="description-container">
                <h1>Descripción del Juego</h1>
                <p>
                    Pocket Imperium es un juego de mesa para 3 personas. Los jugadores deberán aprovechar las oportunidades para expandirse, explorar y exterminar para sacar el máximo provecho. El momento oportuno para construir flotas, cuándo explorar las estrellas y cuándo invadir los sistemas controlados por los oponentes será crucial en su búsqueda por gobernar el Imperio.
                </p>
                {/* Agrega más contenido según sea necesario */}
            </div>
        </div>
    );
}
