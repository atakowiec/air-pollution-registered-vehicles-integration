import {createSlice} from "@reduxjs/toolkit";

export interface UserState {
  isLogged: boolean
  id?: number
  username?: string
  role?: "ADMIN" | "USER"
}

const userSlice = createSlice({
  name: 'user',
  initialState: {
    isLogged: false,
  } as UserState,
  reducers: {
    setUserData(state, action) {
      if (!action.payload) return {isLogged: false}

      state.isLogged = !!action.payload.id
      state.id = action.payload.id
      state.username = action.payload.username
      state.role = action.payload.role
    }
  }
})

export default userSlice.reducer

export const userActions = userSlice.actions