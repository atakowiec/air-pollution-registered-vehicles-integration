import {MainNavbar} from "../../components/MainNavbar.tsx";
import {title} from "../../util/title.ts";
import useApi from "../../hooks/useApi.ts";
import {Row} from "react-bootstrap";
import Container from "react-bootstrap/Container";
import VehiclesBarChart from "./components/VehiclesBarChart.tsx";
import MapAndTable from "./components/MapAndTable.tsx";
import {HomeDataContextProvider} from "./hooks/HomeDataContext.tsx";
import MainLineChart from "./components/MainLineChart.tsx";
import VehiclesPieChart from "./components/VehiclesPieChart.tsx";
import PollutionAirChart from "./components/PollutionAirChart.tsx";
import PollutionRadarChart from "./components/PollutionRadarChart.tsx";
import VehiclesFuelBarChart from "./components/VehiclesFuelBarChart.tsx";
import VehiclesBrandsPieChart from "./components/VehiclesBrandsPieChart.tsx";


export default function Home() {
  title("Home");
  const vehiclesByAreaCode = useApi("/vehicles/counts/by-area-code", "get");
  const deregistereVehiclesByAreaCode= useApi("/vehicles/counts/deregistrations-by-area-code", "get");
  const airPollution= useApi("/air-pollution/counts/average-by-year-voivodeship-indicator", "get")
  const fuelTypes= useApi("/vehicles/counts/by-fuel-type", "get");
  const brands = useApi("/vehicles/counts/top-10-brands", "get");

  return (
    <>
      <MainNavbar/>
      <HomeDataContextProvider>
        <Container className={"mt-5 col-12 col-xxl-8 mx-auto mb-5"}>
          <MapAndTable />
          <MainLineChart />
          <Row className={"mt-5"}>
            <VehiclesBarChart apiData={vehiclesByAreaCode}/>
            <PollutionAirChart apiData={airPollution} />
            <VehiclesPieChart apiData={deregistereVehiclesByAreaCode}/>
            <PollutionRadarChart apiData={airPollution}/>
            <VehiclesFuelBarChart apiData={fuelTypes}/>
            <VehiclesBrandsPieChart apiData={brands}/>
          </Row>
        </Container>
      </HomeDataContextProvider>
    </>
  );
}
