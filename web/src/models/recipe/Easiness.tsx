export function getEasinessName(enumValue: Easiness): string | undefined {
  const enumKeys = Object.keys(Easiness) as Array<keyof typeof Easiness>; 
  return enumKeys.find(key => Easiness[key] === enumValue);
}

export function stringToEasiness(easinessString: string): Easiness {
  switch (easinessString.toUpperCase()) {
    case "EASY":
      return Easiness.EASY;
    case "MEDIUM":
      return Easiness.MEDIUM;
    case "HARD":
      return Easiness.HARD;
  }
  return Easiness.EASY;
}

export enum Easiness {
    EASY, MEDIUM, HARD,
    stringToEasiness
}