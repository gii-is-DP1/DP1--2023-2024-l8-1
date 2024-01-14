import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from "reactstrap";

function handleVisible(setVisible, visible) {
    setVisible(!visible);
}

export default function getErrorModalPlayersDelete(setVisible, visible = false, message = null) {
    if (message) {
        const closeBtn = (
            <button className="close" onClick={() => handleVisible(setVisible, visible)} type="button">
                &times;
            </button>
        );
        return (
            <div>
                <Modal isOpen={visible} toggle={() => handleVisible(setVisible, visible)}
                    keyboard={false}>
                    <ModalHeader toggle={() => handleVisible(setVisible, visible)} close={closeBtn}>Alert!</ModalHeader>
                    <ModalBody>
                        {"No se puede eliminar un jugador que está en partida"}
                    </ModalBody>
                    <ModalFooter>
                        <Button color="primary" onClick={() => handleVisible(setVisible, visible)}>Close</Button>
                    </ModalFooter>
                </Modal>
            </div>
        )
    } else
        return <></>;
}