import {useEffect} from "react";
import {getApi} from "../../axios/axios.ts";
import {useDispatch} from "react-redux";
import {useNavigate} from "react-router-dom";
import {userActions} from "../../store/userSlice.ts";

export default function Logout() {
  const navigate = useNavigate()
  const dispatch = useDispatch()

  useEffect(() => {
    getApi().post("/logout")
      .then(() => dispatch(userActions.setUserData(null)))
      .catch((e) => console.error(e))
      .finally(() => navigate("/"))
  }, []);

  return null
}