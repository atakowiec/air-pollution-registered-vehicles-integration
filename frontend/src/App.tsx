import {useDispatch} from "react-redux";
import {RouterProvider} from "react-router-dom";
import router from "./router/router.tsx";
import {useEffect} from "react";
import {getApi} from "./axios/axios.ts";
import {userActions} from "./store/userSlice.ts";

export default function App() {
  const dispatch = useDispatch();

  useEffect(() => {
    getApi().post("/auth/verify")
      .then(res => dispatch(userActions.setUserData(res.data)))
      .catch(() => dispatch(userActions.setUserData(null)));
  }, []);

  return (
    <RouterProvider router={router}/>
  );
}