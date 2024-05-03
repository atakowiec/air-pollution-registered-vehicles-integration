import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import NavDropdown from 'react-bootstrap/NavDropdown';
import style from "../style/main.module.scss"

export function MainNavbar() {
  return (
    <Navbar expand="lg" className="bg-body-tertiary">
      <Container>
        <Navbar.Brand href="#home">Jakas nazwa</Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav style={{width: "100%"}}>
            <Nav.Link href="/">Main</Nav.Link>
            <NavDropdown title="Import danych" id="basic-nav-dropdown">
              <NavDropdown.Item href="/import/cepik/api">
                Dane CEPIK z api
              </NavDropdown.Item>
              <NavDropdown.Item href="/import/cepik/csv">
                Dane CEPIK z pliku CSV
              </NavDropdown.Item>
              <NavDropdown.Divider />
              <NavDropdown.Item href="/import/pollution/xlsx">
                Zanieczyszczenie powietrza z pliku XLSX
              </NavDropdown.Item>
            </NavDropdown>
            <Nav.Link href="/login" className={style.navbarLogin}>Login</Nav.Link>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}