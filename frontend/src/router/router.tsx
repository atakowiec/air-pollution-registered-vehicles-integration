import {createBrowserRouter} from "react-router-dom";
import Home from "../pages/home/Home.tsx";
import Login from "../pages/login/Login.tsx";

const router = createBrowserRouter([{
  path: "/",
  element: <Home/>
}, {
  path: "/login",
  element: <Login/>
}, {
  path: "/import",
  children: [{
    path: "cepik/api",
    element: <>import z api cepik</>
  }, {
    path: "cepik/csv",
    element: <>import z csv cepik</>
  }, {
    path: "pollution/xlsx",
    element: <>import zanieczyszczenia powietrza z xlsx</>
  }]
}])

export default router