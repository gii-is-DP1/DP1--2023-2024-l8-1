import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Button, ButtonGroup, Table } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import deleteFromPagedList from "../../util/deleteFromPagedList";
import getErrorModalPlayersDelete from "../../util/getErrorModalPlayersDelete";

const jwt = tokenService.getLocalAccessToken();

export default function UserListAdmin() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);

  const [page, setPage] = useState(0);
  const [count, setCount] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [data, setData] = useState({ content: [], size: 10, first: true, last: false });

  const [alerts, setAlerts] = useState([]);

  useEffect(() => {
    fetch(`/api/v1/users/admin?page=${page}&size=${pageSize}`, {
      headers: {
        "Authorization": `Bearer ${jwt}`,
      },
    })
      .then(response => response.json())
      .then(json => {
        if (json.message) {
          setMessage(json.message);
          setVisible(true);
        }
        else {
          setData(json);
          setCount(json.totalElements)
        }
      }).catch((error) => { window.alert(error); });
  }, [page, pageSize]);


  const userList = data.content.filter((user) => user.authority.authority !== 'ADMIN')
  .map((user) => {
    return (
      <tr key={user.id}>
        <td>{user.username}</td>
        <td>{user.authority.authority}</td>
        <td>
          <ButtonGroup>
            <Button
              size="sm"
              color="primary"
              aria-label={"edit-" + user.id}
              tag={Link}
              to={"/users/" + user.id}
            >
              Edit
            </Button>
            <Button
              size="sm"
              color="danger"
              aria-label={"delete-" + user.id}
              onClick={() =>
                deleteFromPagedList(
                  `/api/v1/users/${user.id}`,
                  user.id,
                  [data, setData],
                  [alerts, setAlerts],
                  setMessage,
                  setVisible
                )
              }
            >
              Delete
            </Button>
          </ButtonGroup>
        </td>
      </tr>
    );
  });
  const modal = getErrorModalPlayersDelete(setVisible, visible, message);

  const pageSizes = [5, 10, 20, 100];

  function handlePageChange(newPage) {
    setPage(newPage);
  }


  function handlePageSizeChange(newPageSize) {
    setPage(0); // Reiniciar a la primera página cuando cambie el tamaño de página
    setPageSize(newPageSize);
  }

  return (
    <div className="admin-page-container">
      <h1 className="text-center">Users</h1>
      {alerts.map((a) => a.alert)}
      {modal}
      <Button color="success" tag={Link} to="/users/new">
        Add User
      </Button>
      <div>
        <span style={{ marginRight: "5px" }}>Tamaño de la página:</span>
        <select style={{ marginTop: "10px" }} value={pageSize} onChange={(e) => handlePageSizeChange(e.target.value)}>
          {pageSizes.map((size) => (
            <option key={size} value={size}>
              {size}
            </option>
          ))}
        </select>
        <span style={{ marginLeft: "10px", marginRight: "-8px" }}>Página:</span>
        <select
          style={{ marginLeft: "10px" }}
          value={page}
          onChange={(e) => handlePageChange(Number(e.target.value))}
        >
          {Array.from({ length: Math.ceil(count / pageSize) }, (_, index) => (
            <option key={index} value={index}>
              {index + 1}
            </option>
          ))}
        </select>
      </div>

      <div>
        <Table aria-label="users" className="mt-4">
          <thead>
            <tr>
              <th>Username</th>
              <th>Authority</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>{userList}</tbody>
        </Table>
      </div>
    </div>
  );
}
