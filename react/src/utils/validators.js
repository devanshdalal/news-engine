export const isValidPassword = value => {
  if (value) {
    if (
      // eslint-disable-next-line
      /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%\^&\*])(?=.{6,})/.test(
        value
      )
    ) {
      return false;
    } else {
      return "Password is not strong";
    }
  }
  return "Password is required";
};

export const isValidEmail = value => {
  if (value && /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i.test(value)) {
    return false;
  }
  return "Invalid email address";
};
export const isEmptyString = value => {
  if (value && value.length) {
    return false;
  }
  return "Feild is required";
};
