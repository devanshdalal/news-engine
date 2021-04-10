import { ActionTypes } from "../constants/ActionTypes";

export const logout = () => ({
  type: ActionTypes.USER_LOGOUT
});

export const saveUser = payload => ({
  type: ActionTypes.SAVE_USER,
  payload
});
