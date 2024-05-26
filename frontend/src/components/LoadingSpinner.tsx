import {Spinner} from "react-bootstrap";

export default function LoadingSpinner() {
  return (
    <Spinner animation="border" style={{width: "50px", height: "50px"}} role="status">
      <span className="visually-hidden">Ładowanie danych...</span>
    </Spinner>
  )
}