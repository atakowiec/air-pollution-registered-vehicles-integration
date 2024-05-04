import {configureStore} from "@reduxjs/toolkit";
import userReducer, {UserState} from "./userSlice";

export interface State {
  user: UserState
}


export const store = configureStore<State>({
  reducer: {
    user: userReducer,
  }
})