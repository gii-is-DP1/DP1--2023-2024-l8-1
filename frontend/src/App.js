import React from "react";
import { Route, Routes } from "react-router-dom";
import jwt_decode from "jwt-decode";
import { ErrorBoundary } from "react-error-boundary";
import AppNavbar from "./AppNavbar";
import Home from "./home";
import PrivateRoute from "./privateRoute";
import Register from "./auth/register";
import Login from "./auth/login";
import Logout from "./auth/logout";
import tokenService from "./services/token.service";
import PetEditAdmin from "./admin/pets/PetEditAdmin";
import PetListAdmin from "./admin/pets/PetListAdmin";
import UserListAdmin from "./admin/users/UserListAdmin";
import UserEditAdmin from "./admin/users/UserEditAdmin";
import OwnerListAdmin from "./admin/owners/OwnerListAdmin";
import OwnerEditAdmin from "./admin/owners/OwnerEditAdmin";
import SpecialtyListAdmin from "./admin/vets/SpecialtyListAdmin";
import SpecialtyEditAdmin from "./admin/vets/SpecialtyEditAdmin";
import VetListAdmin from "./admin/vets/VetListAdmin";
import VetEditAdmin from "./admin/vets/VetEditAdmin";
import VisitListAdmin from "./admin/visits/VisitListAdmin";
import VisitEditAdmin from "./admin/visits/VisitEditAdmin";
import ConsultationListAdmin from "./admin/consultations/ConsultationListAdmin";
import TicketListAdmin from "./admin/consultations/TicketListAdmin";
import ConsultationEditAdmin from "./admin/consultations/ConsultationEditAdmin";
import ClinicOwnerListAdmin from "./admin/clinicOwners/ClinicOwnerListAdmin";
import ClinicOwnerEditAdmin from "./admin/clinicOwners/ClinicOwnerEditAdmin";
import ClinicListAdmin from "./admin/clinics/ClinicListAdmin";
import ClinicEditAdmin from "./admin/clinics/ClinicEditAdmin";
import GameList from './game/preGame/gameList';
import CreateGame from "./game/preGame/createGame";
import CreateInvitation from "./invitation/createInvitation";
import InvitationList from "./invitation/invitationList";
import GameLobby from "./game/preGame/gameLobby";
import PlayerDetails from "./player/playerDetails";

function ErrorFallback({ error, resetErrorBoundary }) {
  return (
    <div role="alert">
      <p>Something went wrong:</p>
      <pre>{error.message}</pre>
      <button onClick={resetErrorBoundary}>Try again</button>
    </div>
  )
}

function App() {
  const jwt = tokenService.getLocalAccessToken();
  let roles = []
  if (jwt) {
    roles = getRolesFromJWT(jwt);
  }

  function getRolesFromJWT(jwt) {
    return jwt_decode(jwt).authorities;
  }

  let playerRoutes = <></>;
  let adminRoutes = <></>;
  let userRoutes = <></>;
  let publicRoutes = <></>;

  roles.forEach((role) => {
    if (role === "ADMIN") {
      adminRoutes = (
        <>
          <Route path="/users" exact={true} element={<PrivateRoute><UserListAdmin /></PrivateRoute>} />
          <Route path="/users/:username" exact={true} element={<PrivateRoute><UserEditAdmin /></PrivateRoute>} />
          <Route path="/owners" exact={true} element={<PrivateRoute><OwnerListAdmin /></PrivateRoute>} />
          <Route path="/owners/:id" exact={true} element={<PrivateRoute><OwnerEditAdmin /></PrivateRoute>} />
          <Route path="/clinics" exact={true} element={<PrivateRoute><ClinicListAdmin /></PrivateRoute>} />
          <Route path="/clinics/:id" exact={true} element={<PrivateRoute><ClinicEditAdmin /></PrivateRoute>} />
          <Route path="/clinicOwners" exact={true} element={<PrivateRoute><ClinicOwnerListAdmin /></PrivateRoute>} />
          <Route path="/clinicOwners/:id" exact={true} element={<PrivateRoute><ClinicOwnerEditAdmin /></PrivateRoute>} />
          <Route path="/pets" exact={true} element={<PrivateRoute><PetListAdmin /></PrivateRoute>} />
          <Route path="/pets/:id" exact={true} element={<PrivateRoute><PetEditAdmin /></PrivateRoute>} />
          <Route path="/pets/:petId/visits" exact={true} element={<PrivateRoute><VisitListAdmin /></PrivateRoute>} />
          <Route path="/pets/:petId/visits/:visitId" exact={true} element={<PrivateRoute><VisitEditAdmin /></PrivateRoute>} />
          <Route path="/vets" exact={true} element={<PrivateRoute><VetListAdmin /></PrivateRoute>} />
          <Route path="/vets/:id" exact={true} element={<PrivateRoute><VetEditAdmin /></PrivateRoute>} />
          <Route path="/vets/specialties" exact={true} element={<PrivateRoute><SpecialtyListAdmin /></PrivateRoute>} />
          <Route path="/vets/specialties/:specialtyId" exact={true} element={<PrivateRoute><SpecialtyEditAdmin /></PrivateRoute>} />
          <Route path="/consultations" exact={true} element={<PrivateRoute><ConsultationListAdmin /></PrivateRoute>} />
          <Route path="/consultations/:consultationId" exact={true} element={<PrivateRoute><ConsultationEditAdmin /></PrivateRoute>} />
          <Route path="/consultations/:consultationId/tickets" exact={true} element={<PrivateRoute><TicketListAdmin /></PrivateRoute>} />
        </>)
    }
    if (role === "PLAYER") {
      playerRoutes = (
        <>
          <Route path="/" element={<GameList />} />
          <Route path="/player" element={<PlayerDetails />} />
          <Route path="/game/new" element={<CreateGame />} />
          <Route path="/game/:gameId" element={<CreateGame />} />
          <Route path="/game/" element={<GameList />} />
          <Route path="/game/lobby/:gameId" element={<GameLobby />} />
          <Route path="/invitations/new" element={<CreateInvitation />} />
          <Route path="/invitations" element={<InvitationList />} />
        </>
      )
    }
  })
  if (!jwt) {
    publicRoutes = (
      <>
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
      </>
    )
  } else {
    userRoutes = (
      <>
        <Route path="/logout" element={<Logout />} />
        <Route path="/login" element={<Login />} />
      </>
    )
  }

  return (
    <div>
      <ErrorBoundary FallbackComponent={ErrorFallback} >
        <AppNavbar />
        <Routes>
          <Route path="/" exact={true} element={<Home />} />
          {publicRoutes}
          {userRoutes}
          {adminRoutes}
          {playerRoutes}
        </Routes>
      </ErrorBoundary>
    </div>
  );
}

export default App;
