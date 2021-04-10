import { ActionTypes } from "../constants/ActionTypes";

export const getItems = sourceType => {
  return {
    type: ActionTypes.GET_ITEMS,
    payload: sourceType
  };
};

export const resetStore = () => {
  return {
    type: ActionTypes.RESET_STORE
  };
};

export const clearItems = () => {
  return {
    type: ActionTypes.CLEAR_ITEMS_LIST
  };
};

export const itemsFilterChange = payload => ({
  type: ActionTypes.ITEMS_FILTER_CHANGE,
  payload
});
