import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import NavDropdown from 'react-bootstrap/NavDropdown';
import style from "../style/main.module.scss"
import {useSelector} from "react-redux";
import {State} from "../store";
import {NavLink} from "react-router-dom";

export function MainNavbar() {
  const user = useSelector((state: State) => state.user);

  return (
    <Navbar expand="lg" className="bg-body-tertiary">
      <Container>
        <Navbar.Brand as={NavLink} to="/">Jakas nazwa</Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav"/>
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav style={{width: "100%"}}>
            <Nav.Link href="/">Strona główna</Nav.Link>
            <NavDropdown title="Import danych" id="basic-nav-dropdown">
              <NavDropdown.Item as={NavLink} to="/import/cepik/api">
                Dane CEPIK z api
              </NavDropdown.Item>
              <NavDropdown.Item as={NavLink} to="/import/cepik/csv">
                Dane CEPIK z pliku CSV
              </NavDropdown.Item>
              <NavDropdown.Divider/>
              <NavDropdown.Item as={NavLink} to="/import/pollution/xlsx">
                Zanieczyszczenie powietrza z pliku XLSX
              </NavDropdown.Item>
            </NavDropdown>
            {
              user.isLogged ?
                <>
                  <span className={`${style.navbarLogin} nav-link`}>Zalogowano jako <b>{user.username}</b></span>
                  <Nav.Link as={NavLink} to="/logout">Wyloguj</Nav.Link>
                </> :
                <>
                  <Nav.Link as={NavLink} to="/login" className={style.navbarLogin}>Logowanie</Nav.Link>
                  <Nav.Link as={NavLink} to="/register">Rejestracja</Nav.Link>
                </>
            }
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}