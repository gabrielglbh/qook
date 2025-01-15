export class StringFormatters {
  static generateSubStrings(input: string): string[] {
    const substrings: string[] = [];

    for (let i = 0; i < input.length; i++) {
      for (let j = i + 1; j <= input.length; j++) {
        substrings.push(input.substring(i, j));
      }
    }

    return substrings;
  }
}