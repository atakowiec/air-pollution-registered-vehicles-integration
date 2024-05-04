import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import NavDropdown from 'react-bootstrap/NavDropdown';
import style from "../style/main.module.scss"
import {useSelector} from "react-redux";
import {State} from "../store";

export function MainNavbar() {
  const user = useSelector((state: State) => state.user);

  return (
    <Navbar expand="lg" className="bg-body-tertiary">
      <Container>
        <Navbar.Brand href="#home">Jakas nazwa</Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav"/>
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav style={{width: "100%"}}>
            <Nav.Link href="/">Strona główna</Nav.Link>
            <NavDropdown title="Import danych" id="basic-nav-dropdown">
              <NavDropdown.Item href="/import/cepik/api">
                Dane CEPIK z api
              </NavDropdown.Item>
              <NavDropdown.Item href="/import/cepik/csv">
                Dane CEPIK z pliku CSV
              </NavDropdown.Item>
              <NavDropdown.Divider/>
              <NavDropdown.Item href="/import/pollution/xlsx">
                Zanieczyszczenie powietrza z pliku XLSX
              </NavDropdown.Item>
            </NavDropdown>
            {
              user.isLogged ?
                <>
                  <Nav.Link className={style.navbarLogin}>Zalogowano jako <b>{user.username}</b></Nav.Link>
                  <Nav.Link href="/logout">Wyloguj</Nav.Link>
                </> :
                <>
                  <Nav.Link href="/login" className={style.navbarLogin}>Logowanie</Nav.Link>
                  <Nav.Link href="/register">Rejestracja</Nav.Link>
                </>
            }
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}