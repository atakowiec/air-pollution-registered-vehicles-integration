import {createBrowserRouter} from "react-router-dom";
import Home from "../pages/home/Home.tsx";
import Login from "../pages/login/Login.tsx";
import Logout from "../pages/login/Logout.tsx";
import Register from "../pages/login/Register.tsx";
import CepikApiImport from "../pages/import/cepik/CepikApiImport.tsx";
import CepikFileImport from "../pages/import/cepik/CepikFileImport.tsx";
import PollutionFileImport from "../pages/import/pollution/PollutionFileImport.tsx";
import Legend from "../pages/home/Legend.tsx";

const router = createBrowserRouter([
  {
    path: "/",
    element: <Home/>
  },
  {
    path: "/login",
    element: <Login/>
  },
  {
    path: "/register",
    element: <Register/>
  },
  {
    path: "/logout",
    element: <Logout/>
  },
  {
    path: "/import",
    children: [
      {
        path: "vehicles/api",
        element: <CepikApiImport />
      },
      {
        path: "vehicles/csv",
        element: <CepikFileImport />
      },
      {
        path: "pollution/xlsx",
        element: <PollutionFileImport />
      }
    ]
  },
  {
    path: "/legenda",
    element: <Legend />
  }
])

export default router